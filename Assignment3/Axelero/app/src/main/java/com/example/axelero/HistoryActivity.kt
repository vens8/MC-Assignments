package com.example.axelero

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.axelero.db.AppDatabase
import com.example.axelero.repository.OrientationDataRepository
import com.example.axelero.ui.HistoryContent
import com.example.axelero.ui.theme.AxeleroTheme

class HistoryActivity : ComponentActivity() {
    private val orientationDataRepository by lazy {
        OrientationDataRepository.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HistoryActivity: orientationDataRepository", orientationDataRepository.toString())
        setContent {
            HistoryContent(orientationDataRepository)
        }
    }

    override fun onResume() {
        super.onResume()
        // Pause the sensor updates in the OrientationDataRepository
        orientationDataRepository.pauseSensorUpdates()
    }

    override fun onPause() {
        super.onPause()
        // Resume the sensor updates in the OrientationDataRepository
        orientationDataRepository.resumeSensorUpdates()
    }
}