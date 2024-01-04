package com.jh.presentation.service.music_player

import android.media.session.PlaybackState
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi

@UnstableApi
class MusicPlayerListener(
    private val notificationManager: CustomNotificationManager,
    private val onMediaItemChanged: (MediaItem) -> Unit,
    private val onPlayStateChanged: () -> Unit,
    private val onMusicEnded: () -> Unit
) : Player.Listener {
    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        mediaItem?.let {
            super.onMediaItemTransition(mediaItem, reason)
            notificationManager.refreshNotification()
            onMediaItemChanged(mediaItem)
        }
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        onPlayStateChanged()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        if (playbackState == Player.STATE_ENDED) {
            onMusicEnded()
        }
    }
}