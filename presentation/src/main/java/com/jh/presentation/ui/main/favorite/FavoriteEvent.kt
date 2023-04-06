package com.jh.presentation.ui.main.favorite

sealed class FavoriteEvent {
    object ShowMusicOption : FavoriteEvent()
    object HideMusicOption : FavoriteEvent()
    object InitBottomSheetState : FavoriteEvent()
}