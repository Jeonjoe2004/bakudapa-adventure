package com.bakudapa.adventure.feature.tracking.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.os.Build
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

/**
 * Manager for handling device barometric pressure sensor to calculate accurate altitude.
 */
@Singleton
class ElevationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager = context.getSystemService<SensorManager>()
    private val pressureSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_PRESSURE)

    val altitudeFlow: Flow<Double> = callbackFlow {
        if (sensorManager == null || pressureSensor == null) {
            close()
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_PRESSURE) {
                    val pressure = event.values[0]
                    // Standard formula for altitude from pressure
                    // P = P0 * (1 - h/44330)^5.255
                    // h = 44330 * (1 - (P/P0)^(1/5.255))
                    val altitude = 44330.0 * (1.0 - (pressure / 1013.25).toDouble().pow(1.0 / 5.255))
                    trySend(altitude)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    fun hasBarometer(): Boolean = pressureSensor != null
}
