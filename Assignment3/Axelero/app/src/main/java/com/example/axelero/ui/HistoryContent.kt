package com.example.axelero.ui

import android.app.Activity
import android.content.Intent
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.axelero.db.OrientationData
import com.example.axelero.repository.OrientationDataRepository
import com.example.axelero.ui.components.LineChart
import com.example.axelero.ui.components.SensorDelayDropdownMenu
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun HistoryContent(orientationDataRepository: OrientationDataRepository, createDocumentResult: ActivityResultLauncher<Intent>) {
    val orientationData = produceState<List<OrientationData>>(initialValue = emptyList()) {
        value = orientationDataRepository.getOrientationData()
    }

    val modelProducer1 = remember { CartesianChartModelProducer.build() }
    val modelProducer2 = remember { CartesianChartModelProducer.build() }
    val modelProducer3 = remember { CartesianChartModelProducer.build() }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Orientation Data History")
        Spacer(modifier = Modifier.height(16.dp))

        // Display the three line charts for the actual orientation angles
        LineChart(
            "X Angle",
            orientationData.value.map { it.xAngle },
            modelProducer1
        )
        Spacer(modifier = Modifier.height(16.dp))
        LineChart(
            "Y Angle",
            orientationData.value.map { it.yAngle },
            modelProducer2
        )
        Spacer(modifier = Modifier.height(16.dp))
        LineChart(
            "Z Angle",
            orientationData.value.map { it.zAngle },
            modelProducer3
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // Save the orientation data to a text file on the device
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TITLE, "orientation_data_" + {orientationDataRepository.sensingInterval.toString()} + ".txt")
                }
                createDocumentResult.launch(intent)
            }
        ) {
            Text("Export Data")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // Reset the orientation data in the database
                CoroutineScope(Dispatchers.IO).launch {
                    orientationDataRepository.clearOrientationData()
                }
            }
        ) {
            Text("Reset Data")
        }
    }
}



