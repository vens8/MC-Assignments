package com.example.weathertogo.viewmodel

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class WeatherViewModelQ1 : ViewModel() {
    private val _selectedLatitude = MutableLiveData<Double>()
    val selectedLatitude: LiveData<Double> get() = _selectedLatitude
    private val _selectedLongitude = MutableLiveData<Double>()
    val selectedLongitude: LiveData<Double> get() = _selectedLongitude
    private val _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate> get() = _selectedDate
    private val _weatherData = MutableLiveData<WeatherData?>()
    val weatherData: LiveData<WeatherData?> get() = _weatherData

    private val _weatherInfo = MutableLiveData<WeatherInfo?>()
    val weatherInfo: LiveData<WeatherInfo?> get() = _weatherInfo

    val messages = MutableLiveData<List<String>>(emptyList())


    fun setSelectedLatitude(lat: String) {
        try {
            val latitude = lat.toDouble()
            // Validate for valid latitude ranges (optional)
            _selectedLatitude.value = latitude
        } catch (e: NumberFormatException) {
            // Handle non-numeric input error
        }
    }

    fun setSelectedLongitude(lon: String) {
        try {
            val longitude = lon.toDouble()
            // Validate for valid latitude ranges (optional)
            _selectedLongitude.value = longitude
        } catch (e: NumberFormatException) {
            // Handle non-numeric input error
        }
    }


    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun clearWeatherInfo() {
        _weatherInfo.value = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeatherData() {
        val date = _selectedDate.value
        if (date != null) {
            val today = LocalDate.now()
            val daysPast = ChronoUnit.DAYS.between(date, today)
            Log.d("WeatherViewModel", "Days past: $daysPast")
            val url = when {
                daysPast in 0..5 -> {
                    // Forecast API for recent dates
                    "https://api.open-meteo.com/v1/forecast?latitude=${_selectedLatitude.value}&longitude=${_selectedLongitude.value}&daily=temperature_2m_max,temperature_2m_min&timezone=auto&past_days=5&forecast_days=1"
                }

                daysPast < 0 && daysPast > -16 -> {
                    "https://api.open-meteo.com/v1/forecast?latitude=${_selectedLatitude.value}&longitude=${_selectedLongitude.value}&daily=temperature_2m_max,temperature_2m_min&timezone=auto&past_days=5&forecast_days=${-1 * daysPast + 1}"

                }

                daysPast <= -16 -> {
                    // Historical API for dates more than 15 days in the future (using average of last 10 years)
                    val dateTenYearsAgo = today.minusYears(10)
                    Log.d("WeatherViewModel", "Date 10 years ago: $dateTenYearsAgo")
                    "https://archive-api.open-meteo.com/v1/archive?latitude=${_selectedLatitude.value}&longitude=${_selectedLongitude.value}&start_date=$dateTenYearsAgo&end_date=$today&daily=temperature_2m_max,temperature_2m_min&timezone=auto"
                }

                else -> {
                    // Historical API for older dates
                    "https://archive-api.open-meteo.com/v1/archive?latitude=${_selectedLatitude.value}&longitude=${_selectedLongitude.value}&start_date=$date&end_date=$date&daily=temperature_2m_max,temperature_2m_min&timezone=auto"
                }
            }
            Log.d("WeatherViewModel", "URL: $url")

            viewModelScope.launch {
                try {
                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://api.open-meteo.com/v1/") // Base URL for both forecast and archive APIs
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val weatherService = retrofit.create(WeatherService::class.java)
                    Log.d("WeatherViewModel", "API call started")
                    val response = weatherService.getWeatherData(url) // Call the appropriate API based on date

                    if (response.isSuccessful) {
                        val weatherData = response.body()
                        _weatherData.value = weatherData
                        Log.d("WeatherViewModel", "API call successful")
                        if (weatherData != null) {
                            Log.d("DaysPast", "$daysPast")
                            if (daysPast <= -16) {
                                val tempMaxAverage = String.format("%.2f", weatherData.daily.temperature_2m_max.filterNotNull().average()).toDouble()
                                val tempMinAverage = String.format("%.2f", weatherData.daily.temperature_2m_min.filterNotNull().average()).toDouble()
                                val location = weatherData.timezone.split("/").reversed().joinToString(", ").replace("_", " ")
                                _weatherInfo.value = WeatherInfo(date, _selectedLatitude.value!!, _selectedLongitude.value!!, tempMaxAverage, tempMinAverage, location, true)
                            }
                            else {
                                val dateIndex = weatherData.daily.time.indexOf(date.toString())
                                if (dateIndex != -1) {
                                    val tempMax = weatherData.daily.temperature_2m_max[dateIndex]
                                    val tempMin = weatherData.daily.temperature_2m_min[dateIndex]
                                    val location = weatherData.timezone.split("/").reversed().joinToString(", ").replace("_", " ")
                                    _weatherInfo.value = WeatherInfo(date, _selectedLatitude.value!!, _selectedLongitude.value!!, tempMax, tempMin, location)
                                } else {
                                    // Handle missing date in response
                                    Log.d("WeatherViewModel", "Date not found in response")
                                    val newMessages = messages.value?.toMutableList()
                                    newMessages?.add("Date not found in API JSON response")
                                    messages.value = newMessages!!
                                }
                            }
                        } else {
                            // Handle missing weather data in response
                            Log.d("WeatherViewModel", "Weather data not found in response")
                            val newMessages = messages.value?.toMutableList()
                            newMessages?.add("Weather data not found in API JSON response")
                            messages.value = newMessages!!
                        }
                    } else {
                        Log.d("WeatherViewModel", "API call failed")
                        val newMessages = messages.value?.toMutableList()
                        newMessages?.add("API call failed")
                        messages.value = newMessages!!
                    }
                } catch (e: Exception) {
                    // Handle network errors
                    Log.e("WeatherViewModel", "Network error: ${e.message}")
                    val newMessages = messages.value?.toMutableList()
                    newMessages?.add("Network error: ${e.message}")
                    messages.value = newMessages!!
                }
            }
        } else {
            // Handle missing date error
            Log.e("WeatherViewModel", "Date not selected")
            val newMessages = messages.value?.toMutableList()
            newMessages?.add("Please select a date")
            messages.value = newMessages!!
        }
    }
}

interface WeatherService {
    @GET
    suspend fun getWeatherData(@Url url: String): Response<WeatherData>
}

data class WeatherData(
    val daily_units: DailyUnits,
    val daily: DailyWeather,
    val timezone: String
)

data class DailyUnits(
    val time: String,
    val temperature_2m_max: String,
    val temperature_2m_min: String
)

data class DailyWeather(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>
)

data class WeatherInfo(
    val date: LocalDate,
    val latitude: Double,
    val longitude: Double,
    val tempMax: Double,
    val tempMin: Double,
    val location: String,
    val isAverage: Boolean = false,
    val isOffline: Boolean = false
)