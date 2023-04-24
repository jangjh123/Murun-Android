package com.jh.presentation.ui.main

import com.jh.presentation.enums.LoadingMusicType

data class MainState(
    val trackedCadence: Int = 0,
    val assignedCadence: Int = 0,
    val loadingMusicType: LoadingMusicType = LoadingMusicType.NONE,
    val isRunning: Boolean = false
)