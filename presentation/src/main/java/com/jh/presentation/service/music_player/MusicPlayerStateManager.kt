package com.jh.presentation.service.music_player

import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.Player.REPEAT_MODE_OFF

object MusicPlayerStateManager {
    val musicPlayerState = mutableStateOf(MusicPlayerState())

    fun updateMusicPlayerState(block: (MusicPlayerState) -> MusicPlayerState) {
        musicPlayerState.value = block(musicPlayerState.value)
    }
}

data class MusicPlayerState(
    val isLoading: Boolean = false,
    val isPlaying: Boolean = false,
    val repeatMode: Int = REPEAT_MODE_OFF,
    val isFavoriteList: Boolean = false,
    val currentMediaItem: MediaItem? = null
)