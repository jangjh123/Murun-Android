package com.jh.presentation.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.*
import com.jh.murun.presentation.R
import com.jh.presentation.service.music_player.MusicPlayerService

@UnstableApi
class CustomNotificationManager(
    private val musicPlayerService: MusicPlayerService,
    private val player: ExoPlayer
) : DefaultMediaNotificationProvider(musicPlayerService) {
    private val notificationManager by lazy { musicPlayerService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private lateinit var notificationBuilder: NotificationCompat.Builder

    fun showNotification() {
        val mediaSession = MediaSession.Builder(musicPlayerService, player)
            .setId(System.currentTimeMillis().toString())
            .build()

        notificationBuilder = NotificationCompat.Builder(musicPlayerService, CHANNEL_ID).apply {
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setOngoing(true)
            setSmallIcon(R.drawable.icon)
            setStyle(MediaStyleNotificationHelper.MediaStyle(mediaSession).setShowActionsInCompactView())
        }

        createNotificationChannel()
        musicPlayerService.startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun dismissNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    fun refreshNotification() {
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, "channelId", NotificationManager.IMPORTANCE_LOW)
            notificationChannel.description = "Channel-Description"
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "musicChannel"
        private const val NOTIFICATION_ID = 202303
    }
}