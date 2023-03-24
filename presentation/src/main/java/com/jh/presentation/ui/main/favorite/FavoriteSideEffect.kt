package com.jh.presentation.ui.main.favorite

sealed interface FavoriteSideEffect {
    object StartRunning : FavoriteSideEffect
}