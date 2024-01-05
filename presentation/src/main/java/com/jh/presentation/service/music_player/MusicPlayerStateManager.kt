package com.jh.presentation.service.music_player

import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.Player.REPEAT_MODE_OFF
import com.jh.presentation.enums.RunningMode
import com.jh.presentation.enums.RunningMode.NONE

object MusicPlayerStateManager {
    val musicPlayerState = mutableStateOf(MusicPlayerState())

    fun updateMusicPlayerState(block: (MusicPlayerState) -> MusicPlayerState) {
        musicPlayerState.value = block(musicPlayerState.value)
    }

    fun initializeMusicPlayerState() {
        musicPlayerState.value = MusicPlayerState()
    }
}

data class MusicPlayerState(
    val isLoading: Boolean = false,
    val cadence: Int = 0,
    val runningMode: RunningMode = NONE,
    val isLaunched: Boolean = false,
    val isPlaying: Boolean = false,
    val repeatMode: Int = REPEAT_MODE_OFF,
    val isFavoriteList: Boolean = false,
    val currentMediaItem: MediaItem? = null
)