package com.jh.presentation.ui.main

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.jh.presentation.base.BaseActivity
import com.jh.presentation.enums.RunningMode.*
import com.jh.presentation.service.cadence_tracking.CadenceTrackingService
import com.jh.presentation.service.music_player.MusicPlayerService
import com.jh.presentation.service.music_player.MusicPlayerStateManager.musicPlayerState
import com.jh.presentation.service.music_player.MusicPlayerStateManager.updateMusicPlayerState
import com.jh.presentation.ui.*
import com.jh.presentation.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint

@androidx.annotation.OptIn(UnstableApi::class)
@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var mediaController: ListenableFuture<MediaController>

    @Composable
    override fun InitComposeUi() {
        MainScreen(
            onClickTrackCadence = { trackCadence() },
            onClickAssignCadence = { assignCadence() },
            onPlayFavoriteList = { playFavoriteList() },
            onClickSkipToPrev = { skipToPrev() },
            onClickPlayOrPause = { playOrPause() },
            onClickSkipToNext = { skipToNext() },
            onClickChangeRepeatMode = { changeRepeatMode() },
            onQuitRunning = { quitRunning() }
        )
    }

    private fun trackCadence() {
        startMusicPlayerService()

        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            startService(CadenceTrackingService.newIntent(this@MainActivity))
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), PackageManager.PERMISSION_GRANTED)
            } else {
                // todo : 기기 버전 대응
            }
        }
    }

    private fun assignCadence() {
        startMusicPlayerService()
    }

    private fun playFavoriteList() {
        updateMusicPlayerState {
            it.copy(isFavoriteList = true)
        }

        startMusicPlayerService()
    }

    private fun skipToPrev() {
        startForegroundService(
            MusicPlayerService.newIntent(
                context = this@MainActivity,
                command = MusicPlayerService.COMMAND_SKIP_TO_PREV
            )
        )
    }

    private fun playOrPause() {
        startForegroundService(
            MusicPlayerService.newIntent(
                context = this@MainActivity,
                command = MusicPlayerService.COMMAND_PLAY_OR_PAUSE
            )
        )
    }

    private fun skipToNext() {
        startForegroundService(
            MusicPlayerService.newIntent(
                context = this@MainActivity,
                command = MusicPlayerService.COMMAND_SKIP_TO_NEXT
            )
        )
    }

    private fun changeRepeatMode() {
        startForegroundService(
            MusicPlayerService.newIntent(
                context = this@MainActivity,
                command = MusicPlayerService.COMMAND_CHANGE_REPEAT_MODE
            )
        )
    }

    private fun quitRunning() {
        stopServices()
    }

    private fun startMusicPlayerService() {
        startForegroundService(
            MusicPlayerService.newIntent(
                context = this@MainActivity,
                command = MusicPlayerService.COMMAND_START
            )
        )

        initMediaController()
    }

    private fun initMediaController() {
        val sessionToken = SessionToken(this, ComponentName(this, MusicPlayerService::class.java))
        mediaController = MediaController.Builder(this, sessionToken).buildAsync()
    }

    private fun stopServices() {
        try {
            stopService(MusicPlayerService.newIntent(this@MainActivity))
            stopService(CadenceTrackingService.newIntent(this@MainActivity))
            MediaController.releaseFuture(mediaController)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        if (!musicPlayerState.value.isPlaying) {
            stopServices()
        }

        super.onDestroy()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}