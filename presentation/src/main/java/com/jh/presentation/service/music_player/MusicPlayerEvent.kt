package com.jh.presentation.service.music_player

sealed class MusicPlayerEvent {
    object Launch : MusicPlayerEvent()
    object LoadMusic : MusicPlayerEvent()
    object PlayOrPause : MusicPlayerEvent()
    data class MusicChanged(val isExistsInFavoriteList: Boolean) : MusicPlayerEvent()
    data class MusicExistenceInFavoriteListChanged(val isExists: Boolean) : MusicPlayerEvent()
    object RepeatModeChanged : MusicPlayerEvent()
    object Quit : MusicPlayerEvent()
}