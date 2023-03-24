package com.jh.presentation.ui.main.favorite

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue

@OptIn(ExperimentalMaterialApi::class)
data class FavoriteState(
    val bottomSheetStateValue: ModalBottomSheetValue? = null
)
