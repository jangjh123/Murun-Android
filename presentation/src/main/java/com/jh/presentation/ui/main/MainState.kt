package com.jh.presentation.ui.main

import com.jh.murun.domain.model.Music
import com.jh.presentation.enums.CadenceType

data class MainState(
    val measuredCadence: Int = 0,
    val assignedCadence: Int = 0,
    val cadenceType: CadenceType = CadenceType.NONE,
    val isRunning: Boolean = false,
    val isSnackBarVisible: Boolean = false,
    val error: String? = null,
    val currentMusic: Music? = null
)