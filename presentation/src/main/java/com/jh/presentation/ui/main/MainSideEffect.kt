package com.jh.presentation.ui.main

sealed class MainSideEffect {
    object GoToFavorite : MainSideEffect()
    object TrackCadence : MainSideEffect()
    object StopTrackingCadence : MainSideEffect()
    object PlayMusic:MainSideEffect()
}