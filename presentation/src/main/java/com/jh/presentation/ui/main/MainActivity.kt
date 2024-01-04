package com.jh.presentation.ui.main

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import com.jh.presentation.base.BaseActivity
import com.jh.presentation.enums.LoadingMusicType.*
import com.jh.presentation.service.cadence_tracking.CadenceTrackingService
import com.jh.presentation.service.cadence_tracking.CadenceTrackingService.CadenceTrackingServiceBinder
import com.jh.presentation.service.music_player.MusicPlayerService
import com.jh.presentation.service.music_player.MusicPlayerService.MusicPlayerServiceBinder
import com.jh.presentation.service.music_player.MusicPlayerStateManager.musicPlayerState
import com.jh.presentation.ui.*
import com.jh.presentation.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint

@androidx.annotation.OptIn(UnstableApi::class)
@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var musicPlayerService: MusicPlayerService
    private val musicPlayerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerServiceBinder
            musicPlayerService = binder.getServiceInstance()
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    private lateinit var cadenceTrackingService: CadenceTrackingService
    private val cadenceTrackingServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: CadenceTrackingServiceBinder = service as CadenceTrackingServiceBinder
            cadenceTrackingService = binder.getServiceInstance()
            trackCadence()
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    @Composable
    override fun InitComposeUi() {
        MainScreen(
            musicPlayerState = musicPlayerState.value,
            onClickTrackCadence = { trackCadence() },
            onClickAssignCadence = { assignCadence() },
            onClickSkipToPrev = {},
            onClickPlayOrPause = {},
            onClickSkipToNext = {},
            onClickChangeRepeatMode = {},
            onQuitRunning = {}
        )
    }

    private fun trackCadence() {
        bindMusicPlayerService()

        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            cadenceTrackingService.start(this@MainActivity)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), PackageManager.PERMISSION_GRANTED)
            } else {
                // todo : 기기 버전 대응
            }
        }
    }

    private fun assignCadence() {
        bindMusicPlayerService()
    }

    private fun bindMusicPlayerService() {
        bindService(
            MusicPlayerService.newIntent(this@MainActivity),
            musicPlayerServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onDestroy() {
        try {
            unbindService(cadenceTrackingServiceConnection)
            unbindService(musicPlayerServiceConnection)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onDestroy()
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