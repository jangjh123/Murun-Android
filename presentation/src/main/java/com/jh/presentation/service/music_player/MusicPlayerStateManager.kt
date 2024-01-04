package com.jh.presentation.service.music_player

import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem

object MusicPlayerStateManager {
    val musicPlayerState = mutableStateOf(MusicPlayerState())

    fun update(block: (MusicPlayerState) -> MusicPlayerState) {
        musicPlayerState.value = block(musicPlayerState.value)
    }
}

data class MusicPlayerState(
    val isLaunched: Boolean = false,
    val isLoading: Boolean = false,
    val isPlaying: Boolean = false,
    val isRepeatingOne: Boolean = false,
    val currentMediaItem: MediaItem? = null
)