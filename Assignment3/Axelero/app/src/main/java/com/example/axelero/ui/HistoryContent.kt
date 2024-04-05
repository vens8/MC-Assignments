package com.example.axelero.ui

import android.hardware.SensorManager
import android.os.Environment
import android.widget.Spinner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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

    val predictedOrientationData by remember {
        derivedStateOf {
            predictOrientationData(orientationData.value)
        }
    }

    val modelProducer = remember { CartesianChartModelProducer.build() }

    var selectedInterval by remember { mutableStateOf(SensorManager.SENSOR_DELAY_NORMAL) }

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
            modelProducer
        )
        Spacer(modifier = Modifier.height(16.dp))
        LineChart(
            "Y Angle",
            orientationData.value.map { it.yAngle },
            modelProducer
        )
        Spacer(modifier = Modifier.height(16.dp))
        LineChart(
            "Z Angle",
            orientationData.value.map { it.zAngle },
            modelProducer
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
        // Display the three line charts for the predicted orientation angles
        LineChart(
            "Predicted X Angle",
            predictedOrientationData.first,
            modelProducer
        )
        Spacer(modifier = Modifier.height(16.dp))
        LineChart(
            "Predicted Y Angle",
            predictedOrientationData.second,
            modelProducer
        )
        Spacer(modifier = Modifier.height(16.dp))
        LineChart(
            "Predicted Z Angle",
            predictedOrientationData.third,
            modelProducer
        )

        Spacer(modifier = Modifier.height(16.dp))
        // Allow the user to change the sensing interval
        SensorDelayDropdownMenu(
            orientationData = orientationData.value,
            orientationDataRepository = orientationDataRepository,
            onIntervalChanged = { interval ->
                // Repeat the prediction process with the new interval
                val (newPredictedXAngles, newPredictedYAngles, newPredictedZAngles) =
                    predictOrientationData(orientationData.value)
                // Update your predicted angles here
            }
        )
    }
}

private fun exportOrientationData(orientationData: List<OrientationData>) {
    val externalStorageDir = Environment.getExternalStorageDirectory()
    val file = File(externalStorageDir, "orientation_data.txt")

    // Convert the orientation data to a string format
    val dataString = orientationData.joinToString("\n") { "${it.xAngle}, ${it.yAngle}, ${it.zAngle}, ${it.timestamp}" }

    // Write the data to the text file
    file.writeText(dataString)

    // Display a success message or perform any other necessary actions
    // ...
}

private fun predictOrientationData(
    orientationData: List<OrientationData>
): Triple<List<Float>, List<Float>, List<Float>> {
    // Convert the orientation data to time series data
    val xTimeSeries = TimeSeries.from(
        orientationData.map { it.timestamp.toDouble() },
        orientationData.map { it.xAngle.toDouble() }
    )
    val yTimeSeries = TimeSeries.from(
        orientationData.map { it.timestamp.toDouble() },
        orientationData.map { it.yAngle.toDouble() }
    )
    val zTimeSeries = TimeSeries.from(
        orientationData.map { it.timestamp.toDouble() },
        orientationData.map { it.zAngle.toDouble() }
    )

    // Use Kotlin-TS to predict the next 10 seconds of orientation values
    val predictedXAngles = xTimeSeries.forecast(10).map { it.toFloat() }
    val predictedYAngles = yTimeSeries.forecast(10).map { it.toFloat() }
    val predictedZAngles = zTimeSeries.forecast(10).map { it.toFloat() }

    return Triple(predictedXAngles, predictedYAngles, predictedZAngles)
}
