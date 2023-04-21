package com.jh.presentation.ui.main.favorite

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import com.jh.murun.domain.model.Music

@OptIn(ExperimentalMaterialApi::class)
data class FavoriteState(
    val isLoading: Boolean = false,
    val bottomSheetStateValue: ModalBottomSheetValue? = null,
    val favoriteList: List<Music> = emptyList(),
    val chosenMusic: Music? = null
)
