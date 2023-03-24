package com.jh.presentation.ui.main

sealed interface MainUiEvent {
    object SkipToPrev : MainUiEvent
    object Play : MainUiEvent
    object Pause : MainUiEvent
    object SkipToNext : MainUiEvent
    object RepeatOne : MainUiEvent
    object TrackCadence : MainUiEvent
    object AssignCadence : MainUiEvent
    object StartRunning : MainUiEvent
    object StopRunning : MainUiEvent
}