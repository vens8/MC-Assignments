package com.example.axelero.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.core.content.getSystemService
import com.example.axelero.db.AppDatabase
import com.example.axelero.db.OrientationData
import com.example.axelero.db.OrientationDataDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrientationDataRepository private constructor(
    private val orientationDataDao: OrientationDataDao,
    private val context: Context
) {
    companion object {
        @Volatile
        private var instance: OrientationDataRepository? = null

        fun getInstance(context: Context): OrientationDataRepository {
            return instance ?: synchronized(this) {
                instance ?: OrientationDataRepository(
                    orientationDataDao = AppDatabase.getInstance(context.applicationContext).orientationDataDao(),
                    context = context
                ).also { instance = it }
            }
        }
    }

    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private var accelerometer: Sensor? = null
    private var sensorEventListener: SensorEventListener? = null
    private var lastUpdate: Long = 0
    var sensingInterval: Int = SensorManager.SENSOR_DELAY_NORMAL
        private set

    init {
        Log.d("OrientationDataRepository", "Initializing OrientationDataRepository")
        Log.d("OrientationDataRepository", sensorManager.toString())
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

                    val currentTime = System.currentTimeMillis()
                    if ((currentTime - lastUpdate) > 1000) { // Check if more than 1 second has passed
                        lastUpdate = currentTime
                        // Call the storeOrientationData function from a coroutine
                        CoroutineScope(Dispatchers.IO).launch {
                            storeOrientationData(x, y, z)
                        }
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
        Log.d("OrientationDataRepository", "Sensor updates started")
    }


    fun pauseSensorUpdates() {
        sensorEventListener?.let {
            sensorManager.unregisterListener(it)
            Log.d("OrientationDataRepository", "Sensor updates paused")
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
        Log.d("Storing OrientationDataRepository", "now")
        orientationDataDao.insert(orientationData)
    }

    suspend fun clearOrientationData() {
        orientationDataDao.deleteAll()
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
        // Update the sensing interval whenever it's changed
        sensingInterval = interval
    }

    suspend fun getOrientationData(): List<OrientationData> {
        return orientationDataDao.getAllOrientationData()
    }
}