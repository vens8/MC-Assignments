package com.example.axelero.ui.components

import android.hardware.SensorManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.axelero.repository.OrientationDataRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorDelayDropdownMenu(
    orientationDataRepository: OrientationDataRepository,
    onIntervalChanged: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val items = mapOf(
        SensorManager.SENSOR_DELAY_NORMAL to "Normal",
        SensorManager.SENSOR_DELAY_UI to "UI",
        SensorManager.SENSOR_DELAY_GAME to "Game",
        SensorManager.SENSOR_DELAY_FASTEST to "Fastest"
    )
    var selectedInterval by remember { mutableIntStateOf(items.keys.first()) }

    Box(modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = true })
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = items[selectedInterval].toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            ExposedDropdownMenuDefaults.TrailingIcon(
                expanded = expanded
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.72f)
        ) {
            items.forEach { (interval, label) ->
                DropdownMenuItem(
                    {
                        Text(text = label,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center // Center text
                        )
                    },
                    onClick = {
                        selectedInterval = interval
                        expanded = false
                        orientationDataRepository.changeSensingInterval(interval)
                        onIntervalChanged(interval)
                    })
            }
        }
    }
}
