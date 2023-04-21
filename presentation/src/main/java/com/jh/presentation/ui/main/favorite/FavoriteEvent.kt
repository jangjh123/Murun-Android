package com.jh.presentation.ui.main.favorite

import com.jh.murun.domain.model.Music

sealed class FavoriteEvent {
    data class ShowMusicOption(val music: Music) : FavoriteEvent()
    object HideMusicOption : FavoriteEvent()
    object InitBottomSheetState : FavoriteEvent()
    object LoadFavoriteList : FavoriteEvent()
    data class FavoriteListLoaded(val favoriteList: List<Music>) : FavoriteEvent()
    object DeleteMusic : FavoriteEvent()
}