package com.jh.presentation.ui.main

import android.Manifest
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
import com.jh.presentation.base.BaseActivity
import com.jh.presentation.enums.RunningMode.*
import com.jh.presentation.service.music_player.MusicPlayerService
import com.jh.presentation.ui.*
import com.jh.presentation.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint

@androidx.annotation.OptIn(UnstableApi::class)
@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Composable
    override fun InitComposeUi() {
        MainScreen(
            onClickTrackCadence = { trackCadence() },
            onClickAssignCadence = { assignCadence() },
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
//            cadenceTrackingService.start(this@MainActivity)
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
        startForegroundService(
            MusicPlayerService.newIntent(
                context = this@MainActivity,
                command = MusicPlayerService.COMMAND_QUIT_RUNNING
            )
        )

        stopServices()
    }

    private fun startMusicPlayerService() {
        startForegroundService(
            MusicPlayerService.newIntent(
                context = this@MainActivity,
                command = MusicPlayerService.COMMAND_START
            )
        )
    }

    private fun stopServices() {
        try {
            stopService(MusicPlayerService.newIntent(this@MainActivity))
//            unbindService(cadenceTrackingServiceConnection)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val KEY_IS_RUNNING_STARTED = "isRunningStarted"

        fun newIntent(
            context: Context,
            isRunningStarted: Boolean
        ): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(KEY_IS_RUNNING_STARTED, isRunningStarted)
            }
        }
    }
}