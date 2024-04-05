package com.example.axelero.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.axelero.db.OrientationData
import com.example.axelero.db.OrientationDataDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrientationDataRepository(
    private val orientationDataDao: OrientationDataDao,
    private val context: Context
) {
    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private var accelerometer: Sensor? = null
    private var sensorEventListener: SensorEventListener? = null

    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    fun startSensorUpdates(onSensorUpdated: (x: Float, y: Float, z: Float) -> Unit) {
        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    onSensorUpdated(x, y, z)
                    // Call the storeOrientationData function from a coroutine
                    CoroutineScope(Dispatchers.IO).launch {
                        storeOrientationData(x, y, z)
                    }                
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Handle accuracy changes if needed
            }
        }

        sensorManager.registerListener(
            sensorEventListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }


    fun pauseSensorUpdates() {
        sensorEventListener?.let {
            sensorManager.unregisterListener(it)
        }
    }

    fun resumeSensorUpdates() {
        sensorEventListener?.let {
            sensorManager.registerListener(
                it,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun stopSensorUpdates() {
        sensorEventListener?.let {
            sensorManager.unregisterListener(it)
        }
    }

    private suspend fun storeOrientationData(x: Float, y: Float, z: Float) {
        val orientationData = OrientationData(
            xAngle = x,
            yAngle = y,
            zAngle = z,
            timestamp = System.currentTimeMillis()
        )
        orientationDataDao.insert(orientationData)
    }

    fun changeSensingInterval(interval: Int) {
        sensorEventListener?.let {
            sensorManager.unregisterListener(it)
            sensorManager.registerListener(
                it,
                accelerometer,
                interval
            )
        }
    }

    suspend fun getOrientationData(): List<OrientationData> {
        return orientationDataDao.getAllOrientationData()
    }
}