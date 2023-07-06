package com.jh.presentation.ui.main.favorite

import com.jh.murun.domain.model.Music

sealed class FavoriteSideEffect {
    object StartRunning : FavoriteSideEffect()
    data class ShowToast(val text: String) : FavoriteSideEffect()
    data class UpdateReorderedFavoriteList(val musics: List<Music>) : FavoriteSideEffect()
}