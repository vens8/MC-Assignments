package com.example.axelero.ui

import android.content.Intent
import android.hardware.SensorManager
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.axelero.HistoryActivity
import com.example.axelero.repository.OrientationDataRepository
import com.example.axelero.ui.components.SensorDelayDropdownMenu

@Composable
fun MainContent(xAngle: Float, yAngle: Float, zAngle: Float, orientationDataRepository: OrientationDataRepository) {
    val context = LocalContext.current
    var selectedInterval by remember { mutableIntStateOf(SensorManager.SENSOR_DELAY_NORMAL) }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Real-Time Orientation")
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Text("X: ")
            Text("$xAngle")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text("Y: ")
            Text("$yAngle")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text("Z: ")
            Text("$zAngle")
        }
        Spacer(modifier = Modifier.height(16.dp))
        SensorDelayDropdownMenu(
            orientationDataRepository = orientationDataRepository,
            onIntervalChanged = { interval ->
                selectedInterval = interval
                Toast.makeText(context, "Sensor delay set to $interval", Toast.LENGTH_SHORT).show()
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // Create an Intent to start HistoryActivity
                val intent = Intent(context, HistoryActivity::class.java)
                // Start HistoryActivity
                context.startActivity(intent)
            }
        ) {
            Text("View History")
        }
    }
}