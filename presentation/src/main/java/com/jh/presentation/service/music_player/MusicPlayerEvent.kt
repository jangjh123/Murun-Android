package com.jh.presentation.service.music_player

sealed class MusicPlayerEvent {
    object Launch : MusicPlayerEvent()
    object LoadMusic : MusicPlayerEvent()
    object Play : MusicPlayerEvent()
    object Pause : MusicPlayerEvent()
    object MusicChanged : MusicPlayerEvent()
    object RepeatModeChanged : MusicPlayerEvent()
}