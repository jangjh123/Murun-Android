package com.jh.presentation.ui.main

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import com.jh.presentation.base.BaseActivity
import com.jh.presentation.enums.LoadingMusicType.*
import com.jh.presentation.service.cadence_tracking.CadenceTrackingService
import com.jh.presentation.service.cadence_tracking.CadenceTrackingService.CadenceTrackingServiceBinder
import com.jh.presentation.service.music_player.MusicPlayerService
import com.jh.presentation.service.music_player.MusicPlayerService.MusicPlayerServiceBinder
import com.jh.presentation.service.music_player.MusicPlayerState
import com.jh.presentation.ui.*
import com.jh.presentation.ui.main.favorite.FavoriteActivity
import com.jh.presentation.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(UnstableApi::class)
@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Composable
    override fun InitComposeUi() {
        MainScreen()
    }
    //    private lateinit var musicPlayerService: MusicPlayerService
//    private var isMusicPlayerServiceBinding = false
//    private val playerUiState = mutableStateOf(MusicPlayerState())
//    private val musicPlayerServiceConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            val binder: MusicPlayerServiceBinder = service as MusicPlayerServiceBinder
//            musicPlayerService = binder.getServiceInstance()
//            musicPlayerService.setState(mainState = viewModel.state.value)
//            private lateinit var cadenceTrackingService: CadenceTrackingService
//            private var isCadenceTrackingServiceBinding = false
//            private val cadenceTrackingServiceConnection = object : ServiceConnection {
//                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//                    val binder: CadenceTrackingServiceBinder = service as CadenceTrackingServiceBinder
//                    cadenceTrackingService = binder.getServiceInstance()
//                    trackCadence()
//                }
//
//                override fun onServiceDisconnected(name: ComponentName?) {}
//            }
////            lifecycleScope.launch {
////                repeatOnResumed {
////                    musicPlayerService.state.collectLatest { playerState ->
////                        playerUiState.value = playerState
////                    }
////                }
////            }
//        }
//
//        override fun onServiceDisconnected(name: ComponentName?) {}
//    }



//                    is MainSideEffect.TrackCadence -> {
//                        if (!isCadenceTrackingServiceBinding) {
//                            isCadenceTrackingServiceBinding = true
//                            bindService(Intent(this@MainActivity, CadenceTrackingService::class.java), cadenceTrackingServiceConnection, Context.BIND_AUTO_CREATE)
//                        }
//                    }
//
//                    is MainSideEffect.StopTrackingCadence -> {
//                        runCatching {
//                            cadenceTrackingService.stop()
//                            isCadenceTrackingServiceBinding = false
//                            unbindService(cadenceTrackingServiceConnection)
//                        }
//                    }
//
//                    is MainSideEffect.LaunchMusicPlayer -> {
//                        if (!isMusicPlayerServiceBinding) {
//                            isMusicPlayerServiceBinding = true
//                            bindService(Intent(this@MainActivity, MusicPlayerService::class.java), musicPlayerServiceConnection, Context.BIND_AUTO_CREATE)
//                        }
//                    }
//
//                    is MainSideEffect.QuitMusicPlayer -> {
//                        if (isMusicPlayerServiceBinding) {
//                            isMusicPlayerServiceBinding = false
//
//                            runCatching {
//                                unbindService(musicPlayerServiceConnection)
//                            }
//                        }
//                    }

    private fun trackCadence() {
        CadenceTrackingService.cadenceLiveData.observe(this) {
//            trackedCadence.value = it
        }

        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
//            cadenceTrackingService.start(this@MainActivity)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), PackageManager.PERMISSION_GRANTED)
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        viewModel.getIsStartedRunningWithFavoriteList()
    }

    override fun onDestroy() {
        runCatching {
//            if (isCadenceTrackingServiceBinding) {
//                unbindService(cadenceTrackingServiceConnection)
//            }
//
//            if (isMusicPlayerServiceBinding) {
//                unbindService(musicPlayerServiceConnection)
//            }
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