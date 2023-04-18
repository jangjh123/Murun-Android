package com.jh.presentation.service.music_player

sealed class MusicPlayerEvent {
    object LoadMusic : MusicPlayerEvent()
    object Play : MusicPlayerEvent()
    object Pause : MusicPlayerEvent()
}