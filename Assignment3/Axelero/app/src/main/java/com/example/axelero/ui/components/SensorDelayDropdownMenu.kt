package com.example.axelero.ui.components

import android.hardware.SensorManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.axelero.db.OrientationData
import com.example.axelero.repository.OrientationDataRepository
import com.example.axelero.ui.predictOrientationData

@Composable
fun SensorDelayDropdownMenu(
    orientationData: List<OrientationData>,
    orientationDataRepository: OrientationDataRepository,
    onIntervalChanged: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf(
        SensorManager.SENSOR_DELAY_NORMAL,
        SensorManager.SENSOR_DELAY_UI,
        SensorManager.SENSOR_DELAY_GAME,
        SensorManager.SENSOR_DELAY_FASTEST
    )
    var selectedInterval by remember { mutableIntStateOf(items[0]) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = selectedInterval.toString(),
            modifier = Modifier.fillMaxWidth().clickable(onClick = { expanded = true })
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { interval ->
                DropdownMenuItem(
                    {
                        Text(text = interval.toString())
                    },
                    onClick = {
                    selectedInterval = interval
                    expanded = false
                    orientationDataRepository.changeSensingInterval(interval)
                    onIntervalChanged(interval)
//                    // Repeat the prediction process with the new interval
//                    val (newPredictedXAngles, newPredictedYAngles, newPredictedZAngles) =
//                        predictOrientationData(orientationData)
//                    predictedXAngles = newPredictedXAngles
//                    predictedYAngles = newPredictedYAngles
//                    predictedZAngles = newPredictedZAngles
                })
            }
        }
    }
}
