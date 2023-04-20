package com.jh.presentation.ui.main

sealed class MainSideEffect {
    object GoToFavorite : MainSideEffect()
    object TrackCadence : MainSideEffect()
    object StopTrackingCadence : MainSideEffect()
    object LaunchMusicPlayer : MainSideEffect()
    object QuitMusicPlayer : MainSideEffect()
    object ChangeRepeatMode : MainSideEffect()
    object SkipToPrev : MainSideEffect()
    object PlayOrPause : MainSideEffect()
    object SkipToNext : MainSideEffect()
}