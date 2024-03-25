package com.example.weathertogo.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.weathertogo.ui.components.OutlinedRow
import com.example.weathertogo.ui.utility.isValidLatitude
import com.example.weathertogo.ui.utility.isValidLongitude
import com.example.weathertogo.viewmodel.WeatherViewModelQ2
import java.time.LocalDate
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Q2Screen(
    navController: NavHostController,
    weatherViewModel: WeatherViewModelQ2
) {
    val latitude = remember { mutableStateOf("") }
    val longitude = remember { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }
    val isFormValid = remember { mutableStateOf(false) }
    val buttonEnabled = remember { derivedStateOf { isFormValid.value && !isLoading.value } }

    fun updateFormValidity() {
        isFormValid.value = latitude.value.isNotBlank() &&
                longitude.value.isNotBlank() &&
                isValidLatitude(latitude.value) &&
                isValidLongitude(longitude.value) &&
                weatherViewModel.selectedDate.value != null
    }

    val weatherInfo by weatherViewModel.weatherInfo.observeAsState()
    val messages by weatherViewModel.messages.observeAsState()

    val tempMax = weatherInfo?.tempMax
    val tempMin = weatherInfo?.tempMin
    val location = weatherInfo?.location
    val isAverage = weatherInfo?.isAverage
    val weatherDate = weatherInfo?.date

    if (messages!!.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { weatherViewModel.messages.value = emptyList() },
            title = { Text("Info") },
            text = { Text(messages!!.joinToString("\n")) },
            confirmButton = {
                Button(onClick = { weatherViewModel.messages.value = emptyList() }) {
                    Text("OK")
                }
            },
            icon = {
                Icon(Icons.Filled.Info, contentDescription = "Info")
            }
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Text(text = "WeatherToGo", style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.width(8.dp)) // Add spacer for better positioning
                        Text(
                            text = "Powered by Room Persistence",
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp,
                            color = Color(0xFFFFA500)
                        )
                    }
                },
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
                    modifier = Modifier.fillMaxWidth(0.8f).padding(2.dp),
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
                                if (isValidLatitude(it)) {
                                    weatherViewModel.setSelectedLatitude(it)
                                }
                            },
                            label = { Text("Latitude") },
                            isError = !isValidLatitude(latitude.value),
                            modifier = Modifier.fillMaxWidth(0.6f),

                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        Spacer(modifier = Modifier.padding(2.dp))
                        OutlinedTextField(
                            value = longitude.value,
                            onValueChange = {
                                longitude.value = it
                                if (isValidLongitude(it)) {
                                    weatherViewModel.setSelectedLongitude(it)
                                }
                            },
                            label = { Text("Longitude") },
                            isError = !isValidLongitude(longitude.value),
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
                                            updateFormValidity()
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

                if (tempMax != null && tempMin != null && location != null && weatherDate != null) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(2.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.Gray),
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
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
                        IconButton(
                            onClick = { weatherViewModel.clearWeatherInfo() },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(end = 36.dp)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Close")
                        }
                    }
                }
            }
        }
    )
}