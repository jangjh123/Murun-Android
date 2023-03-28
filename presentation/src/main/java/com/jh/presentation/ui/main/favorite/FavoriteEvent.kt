package com.jh.presentation.ui.main.favorite

sealed interface FavoriteEvent {
    object ShowMusicOption : FavoriteEvent
    object HideMusicOption : FavoriteEvent
    object InitBottomSheetState : FavoriteEvent
}