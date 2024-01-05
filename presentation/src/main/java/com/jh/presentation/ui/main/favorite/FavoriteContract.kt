package com.jh.presentation.ui.main.favorite

import com.jh.murun.domain.model.Music
import com.jh.presentation.base.BaseContract

interface FavoriteContract : BaseContract<FavoriteContract.State, FavoriteContract.Event, FavoriteContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val isBottomSheetShowing: Boolean = false,
        val favoriteList: List<Music> = emptyList(),
        val chosenMusic: Music? = null
    )

    sealed interface Event {
        object OnStarted : Event

        data class OnFavoriteListReordered(val reorderedFavoriteList: List<Music>) : Event

        data class OnClickShowMusicOption(val music: Music) : Event

        object OnClickHideMusicOption : Event

        object OnClickDeleteMusic : Event

        object OnClickStartRunning : Event
    }

    sealed interface Effect {
        object StartRunning : Effect

        data class ShowToast(val text: String) : Effect

        data class UpdateReorderedFavoriteList(val musics: List<Music>) : Effect
    }
}