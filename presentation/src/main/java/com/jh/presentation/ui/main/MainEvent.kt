package com.jh.presentation.ui.main

sealed interface MainEvent {
    object SkipToPrev : MainEvent
    object PlayOrPause : MainEvent
    object SkipToNext : MainEvent
    object RepeatOne : MainEvent
    object TrackCadence : MainEvent
    object AssignCadence : MainEvent
    object StartOrStopRunning : MainEvent
    data class OnCadenceMeasured(val cadence: Int) : MainEvent
}