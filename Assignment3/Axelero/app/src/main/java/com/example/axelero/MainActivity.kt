package com.example.axelero

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.axelero.repository.OrientationDataRepository
import com.example.axelero.ui.MainContent
import com.example.axelero.ui.theme.AxeleroTheme

class MainActivity : ComponentActivity() {
    private val orientationDataRepository by lazy {
        OrientationDataRepository.getInstance(this)
    }
    private var xAngle by mutableFloatStateOf(0f)
    private var yAngle by mutableFloatStateOf(0f)
    private var zAngle by mutableFloatStateOf(0f)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AxeleroTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent(xAngle, yAngle, zAngle, orientationDataRepository)
                }
            }
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