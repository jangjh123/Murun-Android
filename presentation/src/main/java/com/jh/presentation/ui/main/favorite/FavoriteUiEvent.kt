package com.jh.presentation.ui.main.favorite

sealed interface FavoriteUiEvent {
    object ShowMusicOption : FavoriteUiEvent
    object HideMusicOption : FavoriteUiEvent
    object InitBottomSheetState : FavoriteUiEvent
}