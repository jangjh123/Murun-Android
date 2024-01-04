package com.jh.presentation.ui.main

import androidx.media3.common.MediaItem
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

        object OnLongClickQuitRunning : Event

        data class OnClickAddFavoriteMusic(val mediaItem: MediaItem) : Event

        object OnClickSkipToPrev : Event

        object OnClickPlayOrPause : Event

        object OnClickSkipToNext : Event

        object OnClickChangeRepeatMode : Event
    }

    sealed interface Effect {
        object GoToFavorite : Effect

        object TrackCadence : Effect

        object AssignCadence : Effect

        object ChangeRepeatMode : Effect

        object SkipToPrev : Effect

        object PlayOrPause : Effect

        object SkipToNext : Effect

        object QuitRunning : Effect

        data class ShowToast(val text: String) : Effect
    }
}