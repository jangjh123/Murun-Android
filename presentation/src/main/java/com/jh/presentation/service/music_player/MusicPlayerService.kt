package com.jh.presentation.service.music_player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.jh.presentation.service.music_loader.MusicLoaderService
import com.jh.presentation.service.music_loader.MusicLoaderService.MusicLoaderServiceBinder
import com.jh.presentation.service.music_player.MusicPlayerStateManager.initializeMusicPlayerState
import com.jh.presentation.service.music_player.MusicPlayerStateManager.musicPlayerState
import com.jh.presentation.service.music_player.MusicPlayerStateManager.updateMusicPlayerState
import com.jh.presentation.service.notification.PlayerNotificationManager

@UnstableApi
class MusicPlayerService : LifecycleService() {
    private val exoPlayer: ExoPlayer by lazy { ExoPlayer.Builder(this@MusicPlayerService).build() }
    private val notificationManager by lazy { PlayerNotificationManager(this@MusicPlayerService, exoPlayer) }
    private lateinit var musicLoaderService: MusicLoaderService
    private val musicLoaderServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicLoaderServiceBinder
            musicLoaderService = binder.getServiceInstance()
            musicLoaderService.exoPlayer = exoPlayer
            musicLoaderService.loadMusicByBpm()
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        init()

        return MusicPlayerServiceBinder()
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
                notificationManager = notificationManager,
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

        notificationManager.showNotification()
    }

    fun skipToPrev() {
        if (exoPlayer.repeatMode == REPEAT_MODE_ONE) {
            exoPlayer.seekTo(0L)
        } else {
            exoPlayer.seekToPreviousMediaItem()
        }
    }

    fun playOrPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun skipToNext() {
        if (exoPlayer.repeatMode == REPEAT_MODE_ONE) {
            exoPlayer.seekTo(0L)
        } else if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNext()
        } else {
            musicLoaderService.loadMusicByBpm()
        }
    }

    fun changeRepeatMode() {
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

    fun quitRunning() {
        exoPlayer.stop()
        exoPlayer.release()
        notificationManager.dismissNotification()
        initializeMusicPlayerState()

        try {
            unbindService(musicLoaderServiceConnection)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class MusicPlayerServiceBinder : Binder() {
        fun getServiceInstance(): MusicPlayerService {
            return this@MusicPlayerService
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, MusicPlayerService::class.java)
    }
}