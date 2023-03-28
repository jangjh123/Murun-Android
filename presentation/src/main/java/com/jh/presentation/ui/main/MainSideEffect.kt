package com.jh.presentation.ui.main

sealed interface MainSideEffect {
    object GoToFavorite : MainSideEffect
    object TrackCadence : MainSideEffect
    object StopTrackingCadence : MainSideEffect
}