package com.jh.presentation.ui.main.favorite

import com.jh.murun.domain.model.Music

sealed class FavoriteEvent {
    object ShowMusicOption : FavoriteEvent()
    object HideMusicOption : FavoriteEvent()
    object InitBottomSheetState : FavoriteEvent()
    object LoadFavoriteList : FavoriteEvent()
    data class FavoriteListLoaded(val favoriteList: List<Music>) : FavoriteEvent()
}