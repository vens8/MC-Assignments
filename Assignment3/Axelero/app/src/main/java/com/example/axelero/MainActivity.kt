package com.example.axelero

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import com.example.axelero.repository.OrientationDataRepository
import com.example.axelero.ui.MainContent

class MainActivity : ComponentActivity() {
    private val orientationDataRepository by lazy {
        OrientationDataRepository.getInstance(this)
    }
    private var xAngle by mutableFloatStateOf(0f)
    private var yAngle by mutableFloatStateOf(0f)
    private var zAngle by mutableFloatStateOf(0f)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent(xAngle, yAngle, zAngle)
        }
    }

    override fun onResume() {
        super.onResume()
        orientationDataRepository.startSensorUpdates { x, y, z ->
            xAngle = x
            yAngle = y
            zAngle = z
        }
    }

    override fun onPause() {
        super.onPause()
        orientationDataRepository.stopSensorUpdates()
        Log.d("MainActivity", "onPause: Sensor updates stopped")
    }
}