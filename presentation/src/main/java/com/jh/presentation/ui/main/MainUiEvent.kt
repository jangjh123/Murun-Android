package com.jh.presentation.ui.main

sealed interface MainUiEvent {
    object SkipToPrev : MainUiEvent
    object PlayOrPause : MainUiEvent
    object SkipToNext : MainUiEvent
    object RepeatOne : MainUiEvent
    object TrackCadence : MainUiEvent
    object AssignCadence : MainUiEvent
    object StartOrStopRunning : MainUiEvent
}