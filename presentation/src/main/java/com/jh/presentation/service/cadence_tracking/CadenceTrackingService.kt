package com.jh.presentation.service.cadence_tracking

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.jh.presentation.di.DefaultDispatcher
import com.jh.presentation.service.music_loader.MusicLoaderService
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

    private lateinit var sensorManager: SensorManager
    private var stepCount = 0

    inner class CadenceTrackingServiceBinder : Binder() {
        fun getServiceInstance(): CadenceTrackingService {
            return this@CadenceTrackingService
        }
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)

        return CadenceTrackingServiceBinder()
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
            MusicLoaderService.CADENCE = stepCount * 3
            stepCount = 0
            calculateCadence()
        }
    }

    fun start(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        calculateCadence()
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        stepCount = 0
    }

    override fun onUnbind(intent: Intent?): Boolean {
        // todo : 언바인딩
        return super.onUnbind(intent)
    }
}
