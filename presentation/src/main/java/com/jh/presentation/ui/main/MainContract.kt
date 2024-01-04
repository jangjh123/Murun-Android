package com.jh.presentation.ui.main

import com.jh.presentation.base.BaseContract
import com.jh.presentation.enums.LoadingMusicType

interface MainContract : BaseContract<MainContract.State, MainContract.Event, MainContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val assignedCadence: Int = 0,
        val loadingMusicType: LoadingMusicType = LoadingMusicType.NONE,
        val isRunning: Boolean = false
    )

    sealed interface Event {
        object OnClickTrackCadence : Event

        object OnClickAssignCadence : Event

        object OnClickFavorite : Event

        object OnClickStartRunning : Event

        object OnLongClickStopRunning : Event

        object OnClickAddFavoriteMusic : Event

        object OnClickSkipToPrev : Event

        object OnClickPlayOrPause : Event

        object OnClickSkipToNext : Event

        object OnClickChangeRepeatMode : Event
    }

    sealed interface Effect {
        object GoToFavorite : Effect

        object TrackCadence : Effect

        object AssignCadence : Effect

        object StopTrackingCadence : Effect

        object LaunchMusicPlayer : Effect

        object QuitMusicPlayer : Effect

        object ChangeRepeatMode : Effect

        object SkipToPrev : Effect

        object PlayOrPause : Effect

        object SkipToNext : Effect

        object AddFavoriteMusic : Effect

        data class ShowToast(val text: String) : Effect
    }
}