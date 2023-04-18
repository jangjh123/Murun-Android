package com.jh.presentation.ui.main

sealed class MainEvent {
    object SkipToPrev : MainEvent()
    object PlayOrPause : MainEvent()
    object SkipToNext : MainEvent()
    object ChangeRepeatMode : MainEvent()
    object TrackCadence : MainEvent()
    object AssignCadence : MainEvent()
    object ShowSnackBar : MainEvent()
    object HideSnackBar : MainEvent()
    object StartRunning : MainEvent()
    object StopRunning : MainEvent()
    data class SetAssignedCadence(val cadence: Int) : MainEvent()
    data class SetMeasuredCadence(val cadence: Int) : MainEvent()
}