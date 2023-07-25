package com.jh.presentation.service.music_player

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.jh.murun.domain.model.Music
import com.jh.presentation.di.IoDispatcher
import com.jh.presentation.di.MainDispatcher
import com.jh.presentation.enums.LoadingMusicType.*
import com.jh.presentation.service.music_loader.MusicLoaderService
import com.jh.presentation.service.music_loader.MusicLoaderService.MusicLoaderServiceBinder
import com.jh.presentation.ui.main.MainState
import com.jh.presentation.ui.sendEvent
import com.jh.presentation.util.CustomNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class MusicPlayerService : Service() {
    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(this@MusicPlayerService).build().apply {
            addListener(playerListener)
            repeatMode = REPEAT_MODE_ALL
        }
    }

    private val notificationManager by lazy {
        CustomNotificationManager(
            this@MusicPlayerService,
            exoPlayer
        )
    }

    private lateinit var musicLoaderService: MusicLoaderService
    private var isMusicLoaderServiceBinding = false
    private val musicLoaderServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: MusicLoaderServiceBinder = service as MusicLoaderServiceBinder
            musicLoaderService = binder.getServiceInstance()
            initPlayer()
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    private lateinit var mainState: MainState

    private val eventChannel = Channel<MusicPlayerEvent>()
    val state: StateFlow<MusicPlayerState> = eventChannel.receiveAsFlow()
        .runningFold(MusicPlayerState(), ::reduceState)
        .stateIn(
            CoroutineScope(Dispatchers.Main.immediate),
            SharingStarted.Eagerly,
            MusicPlayerState()
        )

    private fun reduceState(state: MusicPlayerState, event: MusicPlayerEvent): MusicPlayerState {
        return when (event) {
            is MusicPlayerEvent.Launch -> {
                state.copy(isLoading = true, isLaunched = true)
            }
            is MusicPlayerEvent.LoadMusic -> {
                state.copy(isLoading = true)
            }
            is MusicPlayerEvent.PlayOrPause -> {
                state.copy(isPlaying = !state.isPlaying)
            }
            is MusicPlayerEvent.MusicChanged -> {
                state.copy(
                    isLoading = false,
                    currentMusic = event.currentMediaItem
                )
            }
            is MusicPlayerEvent.RepeatModeChanged -> {
                state.copy(isRepeatingOne = !state.isRepeatingOne)
            }
            is MusicPlayerEvent.Quit -> {
                state.copy(
                    isLoading = false,
                    isPlaying = false,
                    currentMusic = null
                )
            }
        }
    }

    inner class MusicPlayerServiceBinder : Binder() {
        fun getServiceInstance(): MusicPlayerService {
            return this@MusicPlayerService
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        if (!isMusicLoaderServiceBinding) {
            isMusicLoaderServiceBinding = true
            bindService(
                Intent(this@MusicPlayerService, MusicLoaderService::class.java),
                musicLoaderServiceConnection,
                Context.BIND_AUTO_CREATE
            )
            eventChannel.sendEvent(MusicPlayerEvent.Launch)
        }

        return MusicPlayerServiceBinder()
    }

    fun setState(mainState: MainState) {
        this.mainState = mainState
    }

    private fun initPlayer() {
        when (mainState.loadingMusicType) {
            TRACKING_CADENCE -> {
                musicLoaderService.loadMusicListByBpm(bpm = mainState.trackedCadence)
            }
            ASSIGN_CADENCE -> {
                musicLoaderService.loadMusicListByBpm(bpm = mainState.assignedCadence)
            }
            FAVORITE_LIST -> {
                musicLoaderService.loadFavoriteList()
            }
            NONE -> Unit
        }

        notificationManager.showNotification()
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        collectMusic()
    }

    private fun collectMusic() {
        musicLoaderService.musicFlow.onEach { music ->
            addMusic(music)
        }.launchIn(CoroutineScope(mainDispatcher))
    }

    private fun addMusic(music: Music) {
        val metadata = MediaMetadata.Builder()
            .setTitle(music.title)
            .setArtist(music.artist)
            .setArtworkData(music.image, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
            .setExtras(bundleOf(Pair("music", music)))
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(music.url)
            .setMediaMetadata(metadata)
            .build()

        exoPlayer.addMediaItem(mediaItem)
    }

    fun skipToPrev() {
        if (exoPlayer.repeatMode == REPEAT_MODE_ALL) {
            exoPlayer.seekToPreviousMediaItem()
        } else {
            exoPlayer.seekTo(0L)
        }
    }

    fun playOrPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

//    fun skipToNext() {
//        if (exoPlayer.repeatMode == REPEAT_MODE_ALL) {
//            if (exoPlayer.hasNextMediaItem() && exoPlayer.mediaItemCount != 1) {
//                exoPlayer.seekToNextMediaItem()
//            } else {
//                if (mainState.loadingMusicType == TRACKING_CADENCE) {
//                    musicLoaderService.loadMusicListByBpm(mainState.trackedCadence)
//                } else {
//                    musicLoaderService.loadNextMusicFile()
//                    isIntended = true
//                    eventChannel.sendEvent(MusicPlayerEvent.LoadMusic)
//                }
//            }
//        } else {
//            exoPlayer.seekTo(0L)
//        }
//    }

    fun changeRepeatMode() {
        if (exoPlayer.repeatMode == REPEAT_MODE_ALL) {
            exoPlayer.repeatMode = REPEAT_MODE_ONE
        } else {
            exoPlayer.repeatMode = REPEAT_MODE_ALL
        }

        eventChannel.sendEvent(MusicPlayerEvent.RepeatModeChanged)
    }

    private val playerListener = object : Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            mediaItem?.let {
                super.onMediaItemTransition(mediaItem, reason)
                notificationManager.refreshNotification()

                CoroutineScope(mainDispatcher).launch {
                    eventChannel.sendEvent(
                        MusicPlayerEvent.MusicChanged(exoPlayer.currentMediaItem)
                    )
                }
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        eventChannel.sendEvent(MusicPlayerEvent.Quit)
        exoPlayer.stop()
        notificationManager.dismissNotification()

        if (isMusicLoaderServiceBinding) {
            isMusicLoaderServiceBinding = false
            unbindService(musicLoaderServiceConnection)
        }

        return super.onUnbind(intent)
    }
}