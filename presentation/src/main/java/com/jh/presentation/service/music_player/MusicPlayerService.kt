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

@UnstableApi
class MusicPlayerService : LifecycleService() {
    private val exoPlayer: ExoPlayer by lazy { ExoPlayer.Builder(this@MusicPlayerService).build() }
    private val notificationManager by lazy { CustomNotificationManager(this@MusicPlayerService, exoPlayer) }
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

    fun skipToNext() {
        if (exoPlayer.repeatMode == REPEAT_MODE_ALL) {
            if (exoPlayer.hasNextMediaItem()) {
                exoPlayer.seekToNext()
            }
        } else {
            musicLoaderService.loadMusicByBpm()
        }
    }

    fun changeRepeatMode() {
        if (exoPlayer.repeatMode == REPEAT_MODE_ALL) {
            exoPlayer.repeatMode = REPEAT_MODE_ONE
        } else {
            exoPlayer.repeatMode = REPEAT_MODE_ALL
        }
    }

    inner class MusicPlayerServiceBinder : Binder() {
        fun getServiceInstance(): MusicPlayerService {
            return this@MusicPlayerService
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        exoPlayer.stop()
        exoPlayer.release()
        notificationManager.dismissNotification()

        try {
            unbindService(musicLoaderServiceConnection)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return super.onUnbind(intent)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, MusicPlayerService::class.java)
    }
}