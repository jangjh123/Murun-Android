package com.jh.presentation.ui.main

sealed class MainEvent {
    object TrackCadence : MainEvent()
    object AssignCadence : MainEvent()
    object PlayFavoriteList : MainEvent()
    object StartRunning : MainEvent()
    object StopRunning : MainEvent()
    data class SetAssignedCadence(val cadence: Int) : MainEvent()
}