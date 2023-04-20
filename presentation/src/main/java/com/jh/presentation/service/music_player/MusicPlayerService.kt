package com.jh.presentation.service.music_player

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import androidx.core.os.bundleOf
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.jh.murun.domain.model.Music
import com.jh.presentation.di.MainDispatcher
import com.jh.presentation.enums.CadenceType.ASSIGN
import com.jh.presentation.enums.CadenceType.TRACKING
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
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlayerService : Service() {
    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(this@MusicPlayerService).build().apply {
            addListener(playerListener)
            repeatMode = REPEAT_MODE_ALL
        }
    }
    private val notificationManager by lazy { CustomNotificationManager(this@MusicPlayerService, exoPlayer) }
    private lateinit var musicLoaderService: MusicLoaderService
    private var isMusicLoaderServiceBinding = false
    private val musicLoaderServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: MusicLoaderServiceBinder = service as MusicLoaderServiceBinder
            musicLoaderService = binder.getServiceInstance()
            isMusicLoaderServiceBinding = true
            initPlayer()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isMusicLoaderServiceBinding = false
        }
    }
    private var isStarted = false
    private var isIntended = false

    private lateinit var mainState: MainState

    private val eventChannel = Channel<MusicPlayerEvent>()
    val state: StateFlow<MusicPlayerState> = eventChannel.receiveAsFlow()
        .runningFold(MusicPlayerState(), ::reduceState)
        .stateIn(CoroutineScope(Dispatchers.Main.immediate), SharingStarted.Eagerly, MusicPlayerState())

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
                state.copy(isLoading = false, currentMusic = exoPlayer.currentMediaItem)
            }
            is MusicPlayerEvent.RepeatModeChanged -> {
                state.copy(isRepeatingOne = !state.isRepeatingOne)
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
            bindService(Intent(this@MusicPlayerService, MusicLoaderService::class.java), musicLoaderServiceConnection, Context.BIND_AUTO_CREATE)
            eventChannel.sendEvent(MusicPlayerEvent.Launch)
        }

        return MusicPlayerServiceBinder()
    }

    fun setState(mainState: MainState) {
        this.mainState = mainState
    }

    private fun initPlayer() {
        if (mainState.cadenceType == TRACKING) {

        } else if (mainState.cadenceType == ASSIGN) {
            musicLoaderService.loadMusicListByCadence(cadence = mainState.assignedCadence)
        }

        collectMusicFile()
    }

    private fun collectMusicFile() {
        musicLoaderService.completeMusicFlow.onEach { music ->
            if (music != null) {
                addMusicToPlayer(music)
            } else {
                // TODO : Error Handling
            }
        }.launchIn(CoroutineScope(mainDispatcher))
    }

    private fun addMusicToPlayer(music: Music) {
        val mediaSourceFactory = ProgressiveMediaSource.Factory(DefaultDataSource.Factory(this@MusicPlayerService))
        val metadata = MediaMetadata.Builder()
            .setTitle(music.title)
            .setArtist(music.artist)
            .setArtworkData(music.image, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
            .setExtras(bundleOf(Pair("duration", music.duration)))
            .build()
        val mediaItem = MediaItem.Builder()
            .setUri(music.diskPath)
            .setMediaMetadata(metadata)
            .build()
        val source = mediaSourceFactory.createMediaSource(mediaItem)
        exoPlayer.addMediaSource(source)

        if (!isStarted) {
            launchPlayer()
            isStarted = true
        }

        if (isIntended) {
            exoPlayer.seekToNextMediaItem()
            isIntended = false
        }
    }

    private fun launchPlayer() {
        exoPlayer.prepare()
        exoPlayer.play()
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

        notificationManager.setPlaybackState()
        eventChannel.sendEvent(MusicPlayerEvent.PlayOrPause)
    }

    fun skipToNext() {
        if (exoPlayer.repeatMode == REPEAT_MODE_ALL) {
            if (exoPlayer.hasNextMediaItem() && exoPlayer.mediaItemCount != 1) {
                exoPlayer.seekToNextMediaItem()
            } else {
                musicLoaderService.loadNextMusicFile()
                isIntended = true
                eventChannel.sendEvent(MusicPlayerEvent.LoadMusic)
            }
        } else {
            exoPlayer.seekTo(0L)
        }
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    fun changeRepeatMode() {
        if (exoPlayer.repeatMode == REPEAT_MODE_ALL) {
            exoPlayer.repeatMode = REPEAT_MODE_ONE
        } else {
            exoPlayer.repeatMode = REPEAT_MODE_ALL
        }

        eventChannel.sendEvent(MusicPlayerEvent.RepeatModeChanged)
    }

    private fun convertMetadata(mediaItem: MediaItem): android.media.MediaMetadata {
        return android.media.MediaMetadata.Builder().apply {
            putString(android.media.MediaMetadata.METADATA_KEY_TITLE, mediaItem.mediaMetadata.title.toString())
            putString(android.media.MediaMetadata.METADATA_KEY_ARTIST, mediaItem.mediaMetadata.artist.toString())
            putBitmap(android.media.MediaMetadata.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeByteArray(mediaItem.mediaMetadata.artworkData, 0, mediaItem.mediaMetadata.artworkData?.size ?: 0))
            mediaItem.mediaMetadata.extras?.getLong("duration")?.let { putLong(android.media.MediaMetadata.METADATA_KEY_DURATION, it) }
        }.build()
    }

    private val playerListener = object : Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            mediaItem?.let {
                super.onMediaItemTransition(mediaItem, reason)
                if (!isStarted) {
                    notificationManager.showNotification(convertMetadata(it))
                    eventChannel.sendEvent(MusicPlayerEvent.PlayOrPause)
                } else {
                    notificationManager.setNewMusicPlayback(convertMetadata(it))
                }

                eventChannel.sendEvent(MusicPlayerEvent.MusicChanged)
            }
        }

        override fun onPositionDiscontinuity(oldPosition: PositionInfo, newPosition: PositionInfo, reason: Int) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            if (reason == DISCONTINUITY_REASON_AUTO_TRANSITION) {
                skipToNext()
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        notificationManager.dismissNotification()

        if (isMusicLoaderServiceBinding) {
            unbindService(musicLoaderServiceConnection)
        }

        return super.onUnbind(intent)
    }
}