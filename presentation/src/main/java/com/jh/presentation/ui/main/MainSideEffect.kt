package com.jh.presentation.ui.main

sealed class MainSideEffect {
    object GoToFavorite : MainSideEffect()
    object TrackCadence : MainSideEffect()
    object StopTrackingCadence : MainSideEffect()
    object LaunchMusicPlayer : MainSideEffect()
    object ChangeRepeatMode : MainSideEffect()
    object SkipToPrev : MainSideEffect()
    object Play : MainSideEffect()
    object Pause : MainSideEffect()
    object SkipToNext : MainSideEffect()
}