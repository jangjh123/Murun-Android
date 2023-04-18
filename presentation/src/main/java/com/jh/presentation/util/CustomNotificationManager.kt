package com.jh.presentation.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaMetadata
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.ExoPlayer
import com.jh.murun.presentation.R
import com.jh.presentation.service.MusicPlayerService

class CustomNotificationManager(
    private val musicPlayerService: MusicPlayerService,
    private val player: ExoPlayer
) {
    private val notificationManager by lazy { musicPlayerService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var mediaSession: MediaSessionCompat
    private var currentState: Int = 0

    inner class MurunMediaSessionCallback : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
            musicPlayerService.play()
            mediaSession.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, player.currentPosition, 1f).build())
            currentState = PlaybackStateCompat.STATE_PLAYING
        }

        override fun onPause() {
            super.onPause()
            musicPlayerService.pause()
            mediaSession.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, player.currentPosition, 1f).build())
            currentState = PlaybackStateCompat.STATE_PAUSED
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            musicPlayerService.skipToPrev()
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            musicPlayerService.skipToNext()
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            musicPlayerService.seekTo(pos)
            mediaSession.setPlaybackState(stateBuilder.setState(currentState, pos, 1f).build())
        }
    }

    fun showNotification(metadata: MediaMetadata) {
        stateBuilder = PlaybackStateCompat.Builder().apply {
            setActions(
                PlaybackStateCompat.ACTION_PLAY_PAUSE
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        or PlaybackStateCompat.ACTION_SEEK_TO
            )
            setState(PlaybackStateCompat.STATE_PLAYING, 0L, 1f)
        }
        currentState = PlaybackStateCompat.STATE_PLAYING

        mediaSession = MediaSessionCompat(musicPlayerService, "tag").apply {
            setPlaybackState(stateBuilder.build())
            setMetadata(MediaMetadataCompat.fromMediaMetadata(metadata))
            setCallback(MurunMediaSessionCallback())
            isActive = true
        }

        notificationBuilder = NotificationCompat.Builder(musicPlayerService, CHANNEL_ID).apply {
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setOngoing(true)
            setSmallIcon(R.drawable.icon)
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2, 3)
            )
        }

        createNotificationChannel()
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        musicPlayerService.startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun setMetadata(metadata: MediaMetadata) {
        mediaSession.setMetadata(MediaMetadataCompat.fromMediaMetadata(metadata))
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, "channelId", NotificationManager.IMPORTANCE_LOW)
            notificationChannel.description = "Channel-Description"
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "musicChannel"
        private const val NOTIFICATION_ID = 202303
    }
}