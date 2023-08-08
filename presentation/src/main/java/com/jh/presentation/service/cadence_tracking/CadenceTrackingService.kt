package com.jh.presentation.service.cadence_tracking

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jh.presentation.di.DefaultDispatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class CadenceTrackingService : Service(), SensorEventListener {
    @Inject
    @DefaultDispatcher
    lateinit var defaultDispatcher: CoroutineDispatcher
    private lateinit var sensorManager: SensorManager
    private lateinit var cadenceUpdatingJob: Job
    private var stepCount = 0

    inner class CadenceTrackingServiceBinder : Binder() {
        fun getServiceInstance(): CadenceTrackingService {
            return this@CadenceTrackingService
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return CadenceTrackingServiceBinder()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            stepCount++
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun calculateCadence() {
        cadenceUpdatingJob = CoroutineScope(defaultDispatcher).launch {
            delay(60000L)
            _cadenceLiveData.postValue(stepCount)
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
        cadenceUpdatingJob.cancel()
        stepCount = 0
        _cadenceLiveData.postValue(stepCount)
    }

    companion object {
        private val _cadenceLiveData = MutableLiveData(0)
        val cadenceLiveData: LiveData<Int>
            get() = _cadenceLiveData
    }
}
