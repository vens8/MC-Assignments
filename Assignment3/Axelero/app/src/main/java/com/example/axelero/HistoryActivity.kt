package com.example.axelero

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.axelero.repository.OrientationDataRepository
import com.example.axelero.ui.HistoryContent
import kotlinx.coroutines.launch

class HistoryActivity : ComponentActivity() {
    private val orientationDataRepository by lazy {
        OrientationDataRepository.getInstance(this)
    }

    private val createDocumentResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.also { uri ->
                lifecycleScope.launch {
                    val orientationDataString = orientationDataRepository.getOrientationData().joinToString("\n") { "${it.xAngle}, ${it.yAngle}, ${it.zAngle}, ${it.timestamp}" }
                    contentResolver.openOutputStream(uri)?.writer()?.use {
                        it.write(orientationDataString)
                    }
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HistoryActivity: orientationDataRepository", orientationDataRepository.toString())
        setContent {
            HistoryContent(orientationDataRepository, createDocumentResult)
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