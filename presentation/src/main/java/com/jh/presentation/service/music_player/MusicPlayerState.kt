package com.jh.presentation.service.music_player

import androidx.media3.common.MediaItem

data class MusicPlayerState(
    val isLaunched: Boolean = false,
    val isLoading: Boolean = false,
    val isPlaying: Boolean = false,
    val isRepeatingOne: Boolean = false,
    val currentMediaItem: MediaItem? = null
)