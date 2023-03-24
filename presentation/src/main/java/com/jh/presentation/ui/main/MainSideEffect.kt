package com.jh.presentation.ui.main

sealed interface MainSideEffect {
    object GoToFavorite : MainSideEffect
}