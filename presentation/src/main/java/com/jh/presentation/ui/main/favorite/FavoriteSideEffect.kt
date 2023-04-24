package com.jh.presentation.ui.main.favorite

sealed class FavoriteSideEffect {
    object StartRunning : FavoriteSideEffect()
    data class ShowToast(val text: String) : FavoriteSideEffect()
}