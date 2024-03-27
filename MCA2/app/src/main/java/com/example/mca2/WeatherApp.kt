package com.example.mca2
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun WeatherApp(weatherViewModel: WeatherViewModel) {
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var maxTempStr by remember { mutableStateOf("") }
    var minTempStr by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = latitude,
            onValueChange = { latitude = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter latitude") }
        )
        TextField(
            value = longitude,
            onValueChange = { longitude = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter longitude") }
        )
        TextField(
            value = date,
            onValueChange = { date = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter date") }
        )
        Button(
            onClick = {
                val lat = latitude.toDoubleOrNull() ?: 0.0
                val lon = longitude.toDoubleOrNull() ?: 0.0
                val dateString = date

                weatherViewModel.fetchAndStoreWeatherData(
                    latitude = lat,
                    longitude = lon,
                    date = dateString
                )
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Fetch Weather")
        }

        Text("Maximum Temperature: $maxTempStr")
        Text("Minimum Temperature: $minTempStr")
    }
}
