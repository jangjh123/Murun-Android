package com.jh.presentation.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.os.Binder
import android.os.IBinder
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.jh.murun.domain.model.MusicInfo
import com.jh.presentation.di.MainDispatcher
import com.jh.presentation.enums.CadenceType.ASSIGN
import com.jh.presentation.enums.CadenceType.TRACKING
import com.jh.presentation.service.MusicLoaderService.MusicLoaderServiceBinder
import com.jh.presentation.ui.main.MainState
import com.jh.presentation.util.CustomNotificationManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class MusicPlayerService : Service() {
    private val exoPlayer: ExoPlayer by lazy { ExoPlayer.Builder(this@MusicPlayerService).build() }
    private val notificationManager by lazy { CustomNotificationManager(this@MusicPlayerService, exoPlayer) }
    private lateinit var state: MainState
    private lateinit var metadata: MediaMetadata
    private lateinit var musicLoaderService: MusicLoaderService
    private var isMusicLoaderServiceBinding = false
    private val musicLoaderServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: MusicLoaderServiceBinder = service as MusicLoaderServiceBinder
            musicLoaderService = binder.getServiceInstance()
            isMusicLoaderServiceBinding = true
            setMusicWithCadence()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isMusicLoaderServiceBinding = false
        }
    }

    inner class MusicPlayerServiceBinder : Binder() {
        fun getServiceInstance(): MusicPlayerService {
            return this@MusicPlayerService
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        if (!isMusicLoaderServiceBinding) {
            bindService(Intent(this@MusicPlayerService, MusicLoaderService::class.java), musicLoaderServiceConnection, Context.BIND_AUTO_CREATE)
        }

        return MusicPlayerServiceBinder()
    }

    fun setState(mainState: MainState) {
        state = mainState
    }

    private fun setMusicWithCadence() {
        if (state.cadenceType == TRACKING) {
            // TODO : 곡 끝날 때쯤 리스트 갱신
        } else if (state.cadenceType == ASSIGN) {
            musicLoaderService.loadMusicWithCadence(
                cadence = state.assignedCadence,
                onSuccess = { queue ->
                    if (queue.isNotEmpty()) {
                        setMusic(queue)
                    }
                }
            )
        }
    }

    private fun setMusic(queue: Queue<MusicInfo>) {
        val polled = queue.poll()!!
        queue.offer(polled)

        if (polled.musicPath != null) {
            startMusic(polled.musicPath!!)
        } else {
            musicLoaderService.loadMusicFile(
                musicInfo = polled,
                onWrittenToDisk = { path ->
                    polled.musicPath = path
                    startMusic(path)
                }
            )
        }
    }

    private fun startMusic(musicPath: String) {
        val factory = ProgressiveMediaSource.Factory(DefaultDataSource.Factory(this@MusicPlayerService))
        val source = factory.createMediaSource(MediaItem.fromUri(musicPath))
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(musicPath);
        setMusicMetadata(mediaMetadataRetriever)

        exoPlayer.setMediaSource(source)
        exoPlayer.prepare()
        exoPlayer.play()
        notificationManager.showNotification()
    }

    private fun setMusicMetadata(retriever: MediaMetadataRetriever) {
        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
        val albumCover = retriever.embeddedPicture
        metadata = MediaMetadata.Builder().apply {
            putString(MediaMetadata.METADATA_KEY_TITLE, title)
            putString(MediaMetadata.METADATA_KEY_ARTIST, artist)
            if (duration != null) {
                putLong(MediaMetadata.METADATA_KEY_DURATION, duration)
            }
            if (albumCover != null) {
                putBitmap(MediaMetadata.METADATA_KEY_ART, BitmapFactory.decodeByteArray(albumCover, 0, albumCover.size))
            }
        }.build()
    }

    fun getMusicMetadata() = metadata

    fun playMusic() {
        exoPlayer.play()
    }

    fun pauseMusic() {
        exoPlayer.pause()
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }
}