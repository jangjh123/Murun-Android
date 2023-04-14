package com.jh.presentation.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaMetadata
import android.os.Binder
import android.os.IBinder
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.jh.murun.domain.model.MusicInfo
import com.jh.presentation.di.MainDispatcher
import com.jh.presentation.enums.CadenceType.ASSIGN
import com.jh.presentation.enums.CadenceType.TRACKING
import com.jh.presentation.service.MusicLoaderService.MusicLoaderServiceBinder
import com.jh.presentation.ui.main.MainState
import com.jh.presentation.util.CustomNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlayerService : Service() {
    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    private val exoPlayer: ExoPlayer by lazy { ExoPlayer.Builder(this@MusicPlayerService).build().apply { addListener(playerListener) } }
    private val mediaSourceFactory = ProgressiveMediaSource.Factory(DefaultDataSource.Factory(this@MusicPlayerService))
    private val notificationManager by lazy { CustomNotificationManager(this@MusicPlayerService, exoPlayer) }
    private lateinit var state: MainState
    private lateinit var musicLoaderService: MusicLoaderService
    private var isMusicLoaderServiceBinding = false
    private val musicLoaderServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: MusicLoaderServiceBinder = service as MusicLoaderServiceBinder
            musicLoaderService = binder.getServiceInstance()
            isMusicLoaderServiceBinding = true
            initPlayer()
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

    private fun initPlayer() {
        if (state.cadenceType == TRACKING) {
            // 케이던스 변경시 갱신
        } else if (state.cadenceType == ASSIGN) {
            musicLoaderService.loadMusicInfoListByCadence(cadence = state.assignedCadence)
        }

        collectMusicFile()
    }

    private fun collectMusicFile() {
        musicLoaderService.completeMusicFlow.onEach { musicInfo ->
            addMusicToPlayer(musicInfo)
        }.launchIn(CoroutineScope(mainDispatcher))
    }

    private fun addMusicToPlayer(musicInfo: MusicInfo) {
        val source = mediaSourceFactory.createMediaSource(MediaItem.fromUri(musicInfo.diskPath!!)).apply {
            // metadata 교체 로직 추가
        }
        exoPlayer.addMediaSource(mediaSourceFactory.createMediaSource(MediaItem.fromUri(musicInfo.diskPath!!)))
    }

    private fun launchPlayer() {
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun play() {
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun skipToPrev() {
//        playPreviousMusic()
    }

    fun skipToNext() {
//        playNextMusic()
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    private fun convertMetadata(mediaItem: MediaItem): MediaMetadata {
        return MediaMetadata.Builder().apply {
            putString(MediaMetadata.METADATA_KEY_TITLE, mediaItem.mediaMetadata.title.toString())
            putString(MediaMetadata.METADATA_KEY_ARTIST, mediaItem.mediaMetadata.artist.toString())
//            putBitmap(MediaMetadata.METADATA_KEY_ART, BitmapFactory.decodeByteArray(mediaItem.mediaMetadata.artworkData, 0, mediaItem.mediaMetadata.artworkData?.size ?: 0))
        }.build()
    }

    private val playerListener = object : Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            when (reason) {
                MEDIA_ITEM_TRANSITION_REASON_REPEAT -> {

                }
                MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
                    if (mediaItem?.mediaMetadata != null) {
                        notificationManager.showNotification(convertMetadata(mediaItem))
                    }
                }
                MEDIA_ITEM_TRANSITION_REASON_SEEK -> {

                }
                MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> {
                    if (!exoPlayer.isPlaying) {
                        if (mediaItem != null) {
                            launchPlayer()
                            notificationManager.showNotification(convertMetadata(mediaItem))
                        }
                    }
                }
            }
        }
    }
}