package com.jh.presentation.service.music_player

import com.google.android.exoplayer2.MediaItem

data class MusicPlayerState(
    val isLaunched: Boolean = false,
    val isLoading: Boolean = false,
    val isPlaying: Boolean = false,
    val isRepeatingOne: Boolean = false,
    val currentMusic: MediaItem? = null
)