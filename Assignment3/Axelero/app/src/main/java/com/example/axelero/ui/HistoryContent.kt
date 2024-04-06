package com.example.axelero.ui

import android.hardware.SensorManager
import android.os.Environment
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
import com.example.axelero.db.OrientationData
import com.example.axelero.repository.OrientationDataRepository
import com.example.axelero.ui.components.LineChart
import com.example.axelero.ui.components.SensorDelayDropdownMenu
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import java.io.File

@Composable
fun HistoryContent(orientationDataRepository: OrientationDataRepository) {
    val orientationData = produceState<List<OrientationData>>(initialValue = emptyList()) {
        value = orientationDataRepository.getOrientationData()
    }

    val modelProducer1 = remember { CartesianChartModelProducer.build() }
    val modelProducer2 = remember { CartesianChartModelProducer.build() }
    val modelProducer3 = remember { CartesianChartModelProducer.build() }

    var selectedInterval by remember { mutableIntStateOf(SensorManager.SENSOR_DELAY_NORMAL) }

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
                // Export the orientation data to a text file
                exportOrientationData(orientationData)
            }
        ) {
            Text("Export Data")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Allow the user to change the sensing interval
        SensorDelayDropdownMenu(
            orientationData = orientationData.value,
            orientationDataRepository = orientationDataRepository,
            onIntervalChanged = { interval ->
                selectedInterval = interval
                // Repeat the prediction process with the new interval
//                val (newPredictedXAngles, newPredictedYAngles, newPredictedZAngles) =
//                    predictOrientationData(orientationData.value)
                // Update your predicted angles here
            }
        )
    }
}

private fun exportOrientationData(orientationData: State<List<OrientationData>>) {
    val externalStorageDir = Environment.getExternalStorageDirectory()
    val file = File(externalStorageDir, "orientation_data.txt")

    // Convert the orientation data to a string format
    val dataString = orientationData.value.joinToString("\n") { "${it.xAngle}, ${it.yAngle}, ${it.zAngle}, ${it.timestamp}" }

    // Write the data to the text file
    file.writeText(dataString)

    // Display a success message or perform any other necessary actions
    // ...
}
