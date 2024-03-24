package com.example.weathertogo.ui

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.weathertogo.viewmodel.WeatherViewModel
import java.time.LocalDate
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Q1Screen(navController: NavHostController, weatherViewModel: WeatherViewModel) {
    val latitude = remember { mutableStateOf("") }
    val longitude = remember { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(false) }

    val buttonEnabled = remember {
        derivedStateOf {
            latitude.value.isNotBlank() && longitude.value.isNotBlank() && isNumeric(latitude.value) && isNumeric(
                longitude.value
            ) && weatherViewModel.selectedDate.value != null && !isLoading.value
        }
    }

    val weatherInfo by weatherViewModel.weatherInfo.observeAsState()

    val tempMax = weatherInfo?.tempMax
    val tempMin = weatherInfo?.tempMin
    val location = weatherInfo?.location
    val isAverage = weatherInfo?.isAverage
    val weatherDate = weatherInfo?.date

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "WeatherToGo", style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("landingScreen") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.padding(8.dp))
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(0.8f).padding(2.dp), // Add padding around the card for better spacing
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center // Center content vertically
                    ) {
                        Text(
                            text = "Enter Location Details",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        OutlinedTextField(
                            value = latitude.value,
                            onValueChange = {
                                latitude.value = it
                                if (isNumeric(it)) {
                                    weatherViewModel.setSelectedLatitude(it)
                                }
                            },
                            label = { Text("Latitude") },
                            isError = !isNumeric(latitude.value),
                            modifier = Modifier.fillMaxWidth(0.6f),

                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        Spacer(modifier = Modifier.padding(2.dp))
                        OutlinedTextField(
                            value = longitude.value,
                            onValueChange = {
                                longitude.value = it
                                if (isNumeric(it)) {
                                    weatherViewModel.setSelectedLongitude(it)
                                }
                            },
                            label = { Text("Longitude") },
                            isError = !isNumeric(longitude.value),
                            modifier = Modifier.fillMaxWidth(0.6f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(8.dp))
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(0.8f).padding(2.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Gray),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Select Date",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Button(
                            onClick = { showDatePicker = true },
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .align(Alignment.CenterHorizontally),
                        ) {
                            Text(text = "Choose")
                        }
                        if (showDatePicker) {
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker = false },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            val selectedDate = Calendar.getInstance().apply {
                                                timeInMillis = datePickerState.selectedDateMillis!!
                                            }
                                            weatherViewModel.setSelectedDate(
                                                LocalDate.of(
                                                    selectedDate.get(Calendar.YEAR),
                                                    selectedDate.get(Calendar.MONTH) + 1,
                                                    selectedDate.get(Calendar.DAY_OF_MONTH)
                                                )
                                            )
                                            showDatePicker = false
                                        }
                                    ) { Text("OK") }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { showDatePicker = false }
                                    ) { Text("Cancel") }
                                }
                            ) {
                                DatePicker(state = datePickerState)
                            }
                        }
                        val selectedDate = weatherViewModel.selectedDate.value
                        if (selectedDate != null) {
                            Text(
                                "Selected Date: $selectedDate",
                            style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Button(
                    onClick = {
                        isLoading.value = true
                        weatherViewModel.getWeatherData()
                        isLoading.value = false
                    },
                    enabled = buttonEnabled.value,
                    modifier = Modifier.fillMaxWidth(0.5f),
                    shape = RoundedCornerShape(20)
                ) {
                    if (isLoading.value) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text(text = "Get Weather Data")
                    }
                }
                Spacer(modifier = Modifier.padding(8.dp))

                if (tempMax != null && tempMin != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.8f) // Reduced width of the card
                            .padding(2.dp), // Added padding around the card for better spacing
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.Gray)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally
                            verticalArrangement = Arrangement.Center // Center content vertically
                        ) {
                            Text(
                                text = "Your Weather Report",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            OutlinedRow("Date:", "$weatherDate")
                            OutlinedRow("Location/Timezone:", "$location")
                            OutlinedRow("Max Temp:", "$tempMax°C")
                            OutlinedRow("Min Temp:", "$tempMin°C")
                            Text(
                                text = if (isAverage == true) {
                                    "Note: Using Average Temperatures (Past 10 Years)"
                                } else {
                                    ""
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Yellow,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}
fun isNumeric(str: String): Boolean {
    return str.matches(Regex("[-+]?[0-9]+\\.?[0-9]*"))
}
