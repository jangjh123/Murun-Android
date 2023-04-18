package com.jh.presentation.ui.main

import androidx.compose.ui.graphics.ImageBitmap
import com.jh.presentation.enums.CadenceType

data class MainState(
    val measuredCadence: Int = 0,
    val assignedCadence: Int = 0,
    val cadenceType: CadenceType = CadenceType.NONE,
    val isRunning: Boolean = false,
    val isPlaying: Boolean = false,
    val isRepeatingOne: Boolean = false,
    val musics: ArrayList<String> = ArrayList(),
    val isSnackBarVisible: Boolean = false,
    val error: String? = null
)