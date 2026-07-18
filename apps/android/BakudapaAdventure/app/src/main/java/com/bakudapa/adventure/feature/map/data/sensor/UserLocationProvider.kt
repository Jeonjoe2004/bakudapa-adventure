package com.bakudapa.adventure.feature.map.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private val _locationChannel = Channel<Location>()
    val locationFlow: Flow<Location> = _locationChannel.receiveAsFlow()

    suspend fun startUpdates() {
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
                .setMinUpdateIntervalMillis(1000)
                .build(),
            object : LocationCallback() {},
            Looper.getMainLooper()
        )
    }

    suspend fun stopUpdates() {
        // Auto-cleaned
    }

    suspend fun getLastLocation(): Location? {
        return fusedLocationClient.lastLocation.await()
    }

    fun hasRotationSensor(): Boolean = rotationSensor != null

    fun getRotationFlow() = callbackFlow<Float> {
        if (rotationSensor == null) {
            close()
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    val orientationValues = FloatArray(3)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientationValues)
                    val azimuthDegrees = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
                    trySend(azimuthDegrees)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(listener, rotationSensor!!, SensorManager.SENSOR_DELAY_UI)

        awaitClose { sensorManager.unregisterListener(listener) }
    }
}