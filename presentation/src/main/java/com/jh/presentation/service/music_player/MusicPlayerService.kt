package com.jh.presentation.service.music_player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.jh.presentation.service.music_loader.MusicLoaderService
import com.jh.presentation.service.music_loader.MusicLoaderService.MusicLoaderServiceBinder
import com.jh.presentation.service.music_player.MusicPlayerStateManager.initializeMusicPlayerState
import com.jh.presentation.service.music_player.MusicPlayerStateManager.musicPlayerState
import com.jh.presentation.service.music_player.MusicPlayerStateManager.updateMusicPlayerState

@UnstableApi
class MusicPlayerService : MediaSessionService() {
    private val exoPlayer: ExoPlayer by lazy { ExoPlayer.Builder(this@MusicPlayerService).build() }
    private var mediaSession: MediaSession? = null

    private lateinit var musicLoaderService: MusicLoaderService
    private val musicLoaderServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicLoaderServiceBinder
            musicLoaderService = binder.getServiceInstance()
            musicLoaderService.exoPlayer = exoPlayer

            if (musicPlayerState.value.isFavoriteList) {
                musicLoaderService.loadFavoriteList()
            } else {
                musicLoaderService.loadMusicByBpm()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSession.Builder(this@MusicPlayerService, exoPlayer).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.extras?.getString(KEY_COMMAND)?.let { command ->
            when (command) {
                COMMAND_START -> init()
                COMMAND_SKIP_TO_PREV -> skipToPrev()
                COMMAND_PLAY_OR_PAUSE -> playOrPause()
                COMMAND_SKIP_TO_NEXT -> skipToNext()
                COMMAND_CHANGE_REPEAT_MODE -> changeRepeatMode()
                COMMAND_QUIT_RUNNING -> quitRunning()
            }
        }

        return START_STICKY
    }

    private fun init() {
        initMusicPlayer()
        bindMusicLoaderService()
    }

    private fun bindMusicLoaderService() {
        bindService(
            Intent(this@MusicPlayerService, MusicLoaderService::class.java),
            musicLoaderServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    private fun initMusicPlayer() {
        exoPlayer.apply {
            val musicPlayerListener = MusicPlayerListener(
                onMusicEnded = {
                    if (::musicLoaderService.isInitialized) {
                        musicLoaderService.loadMusicByBpm()
                    }
                }
            )

            addListener(musicPlayerListener)
            prepare()
            repeatMode = REPEAT_MODE_OFF
            playWhenReady = true
        }

        updateMusicPlayerState {
            it.copy(isLaunched = true)
        }
    }

    private fun skipToPrev() {
        if (exoPlayer.repeatMode == REPEAT_MODE_ONE) {
            exoPlayer.seekTo(0L)
        } else {
            exoPlayer.seekToPreviousMediaItem()
        }
    }

    private fun playOrPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    private fun skipToNext() {
        if (exoPlayer.repeatMode == REPEAT_MODE_ONE) {
            exoPlayer.seekTo(0L)
        } else if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNext()
        } else {
            if (musicPlayerState.value.isFavoriteList) {
                exoPlayer.seekToNext()
            } else {
                musicLoaderService.loadMusicByBpm()
            }
        }
    }

    private fun changeRepeatMode() {
        if (exoPlayer.repeatMode == REPEAT_MODE_ONE) {
            if (musicPlayerState.value.isFavoriteList) {
                exoPlayer.repeatMode = REPEAT_MODE_ALL
                updateMusicPlayerState {
                    it.copy(repeatMode = REPEAT_MODE_ALL)
                }
            } else {
                exoPlayer.repeatMode = REPEAT_MODE_OFF
                updateMusicPlayerState {
                    it.copy(repeatMode = REPEAT_MODE_OFF)
                }
            }
        } else {
            exoPlayer.repeatMode = REPEAT_MODE_ONE
            updateMusicPlayerState {
                it.copy(repeatMode = REPEAT_MODE_ONE)
            }
        }
    }

    private fun quitRunning() {
        mediaSession?.run {
            release()
            mediaSession = null
        }

        exoPlayer.release()
        initializeMusicPlayerState()

        try {
            unbindService(musicLoaderServiceConnection)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        quitRunning()
        super.onDestroy()
    }

    companion object {
        private const val KEY_COMMAND = "key_command"

        const val COMMAND_START = "command_start"
        const val COMMAND_SKIP_TO_PREV = "command_skip_to_prev"
        const val COMMAND_PLAY_OR_PAUSE = "command_play_or_pause"
        const val COMMAND_SKIP_TO_NEXT = "command_skip_to_next"
        const val COMMAND_CHANGE_REPEAT_MODE = "command_change_repeat_mode"
        const val COMMAND_QUIT_RUNNING = "command_quit_running"

        fun newIntent(context: Context, command: String? = null): Intent = Intent(context, MusicPlayerService::class.java).apply {
            putExtra(KEY_COMMAND, command)
        }
    }
}