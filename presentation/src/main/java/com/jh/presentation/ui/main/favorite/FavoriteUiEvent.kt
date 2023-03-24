package com.jh.presentation.ui.main.favorite

sealed interface FavoriteUiEvent {
    object OpenBottomSheet : FavoriteUiEvent
    object CloseBottomSheet : FavoriteUiEvent
}