package com.jh.presentation.service.music_player

import androidx.media3.common.MediaItem


sealed class MusicPlayerEvent {
    object Launch : MusicPlayerEvent()
    object LoadMusic : MusicPlayerEvent()
    object PlayOrPause : MusicPlayerEvent()
    data class MusicChanged(val currentMediaItem: MediaItem?) : MusicPlayerEvent()
    object RepeatModeChanged : MusicPlayerEvent()
    object Quit : MusicPlayerEvent()
}