package com.jh.presentation.service

import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.os.Binder
import android.os.IBinder
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.jh.murun.domain.use_case.music.GetMusicListByCadenceUseCase
import com.jh.presentation.util.CustomNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlayerService @Inject constructor(
    private val exoPlayer: ExoPlayer
) : Service() {
    private val notificationManager = CustomNotificationManager(this@MusicPlayerService, exoPlayer)
    private lateinit var metadata: MediaMetadata

    inner class MusicServiceBinder : Binder() {
        fun getServiceInstance(): MusicPlayerService {
            return this@MusicPlayerService
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return MusicServiceBinder()
    }

    fun setMusic(uri: String) {
        val factory = ProgressiveMediaSource.Factory(DefaultDataSource.Factory(this@MusicPlayerService))
        val source = factory.createMediaSource(MediaItem.fromUri(uri))
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(uri)
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