package com.jh.presentation.service.music_player

sealed class MusicPlayerEvent {
    object Launch : MusicPlayerEvent()
    object LoadMusic : MusicPlayerEvent()
    object PlayOrPause : MusicPlayerEvent()
    data class MusicChanged(val isCurrentMusicStored: Boolean) : MusicPlayerEvent()
    data class ChangeMusicIsStoredOrNot(val isCurrentMusicStored: Boolean) : MusicPlayerEvent()
    object RepeatModeChanged : MusicPlayerEvent()
    object Quit : MusicPlayerEvent()
}