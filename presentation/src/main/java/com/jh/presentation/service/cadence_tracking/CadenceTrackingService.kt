package com.jh.presentation.service.cadence_tracking

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.jh.presentation.di.DefaultDispatcher
import com.jh.presentation.service.music_player.MusicPlayerStateManager.musicPlayerState
import com.jh.presentation.service.music_player.MusicPlayerStateManager.updateMusicPlayerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CadenceTrackingService : LifecycleService(), SensorEventListener {
    @Inject
    @DefaultDispatcher
    lateinit var defaultDispatcher: CoroutineDispatcher
    private val sensorManager by lazy { this@CadenceTrackingService.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private var stepCount = 0

    override fun onCreate() {
        super.onCreate()
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        calculateCadence()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                stepCount++
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun calculateCadence() {
        lifecycleScope.launch(defaultDispatcher) {
            delay(20000L)
            updateMusicPlayerState {
                it.copy(cadence = stepCount * 3)
            }

            stepCount = 0
            calculateCadence()
        }
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        stepCount = 0
        super.onDestroy()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, CadenceTrackingService::class.java)
    }
}
