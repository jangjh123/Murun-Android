package com.jh.presentation.service.music_player

sealed class MusicPlayerEvent {
    object Launch : MusicPlayerEvent()
    object LoadMusic : MusicPlayerEvent()
    object PlayOrPause : MusicPlayerEvent()
    object MusicChanged : MusicPlayerEvent()
    object RepeatModeChanged : MusicPlayerEvent()
    object Quit : MusicPlayerEvent()
}