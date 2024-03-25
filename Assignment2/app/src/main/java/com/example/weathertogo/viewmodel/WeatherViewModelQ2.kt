package com.example.weathertogo.viewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertogo.db.WeatherDataEntity
import com.example.weathertogo.db.WeatherDatabase
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class WeatherViewModelQ2(context: Context) : ViewModel() {

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

    private val database = WeatherDatabase.getInstance(context)
    private val weatherDao = database.weatherDao()

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeatherData(context: Context) {
        val date = _selectedDate.value
        if (date != null) {
            if (hasInternetConnection(context)) {
                // Make API call and store data in database
                viewModelScope.launch {
                    try {
                        val retrofit = Retrofit.Builder()
                            .baseUrl("https://api.open-meteo.com/v1/") // Base URL for both forecast and archive APIs
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()

                        val weatherService = retrofit.create(WeatherService::class.java)
                        Log.d("WeatherViewModel", "API call started")
                        val url = getWeatherUrl(date)
                        val response =
                            weatherService.getWeatherData(url) // Call the appropriate API based on date

                        if (response.isSuccessful) {
                            val weatherData = response.body()
                            _weatherData.value = weatherData
                            Log.d("WeatherViewModel", "API call successful")
                            if (weatherData != null) {
                                val weatherDataEntity = WeatherDataEntity(
                                    date,
                                    _selectedLatitude.value!!,
                                    _selectedLongitude.value!!,
                                    extractTempMax(weatherData),
                                    extractTempMin(weatherData),
                                    extractLocation(weatherData),
                                    isFutureAndAverage(date)
                                )
                                try {
                                    weatherDao.insertWeatherData(weatherDataEntity)
                                } catch (e: Exception) {
                                    // Handle database errors
                                    Log.e("WeatherViewModel", "Database error: ${e.message}")
                                }
                            } else {
                                // Handle missing weather data in response
                                Log.d("WeatherViewModel", "Weather data not found in response")
                            }
                        } else {
                            Log.d("WeatherViewModel", "API call failed")
                        }
                    } catch (e: Exception) {
                        // Handle network errors
                        Log.e("WeatherViewModel", "Network error: ${e.message}")
                    }
                }
            } else {
                // Fetch data from database
                fetchWeatherDataFromDatabase(context, date)
            }
        } else {
            // Handle missing date error
            Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT).show()
        }
    }

    fun fetchWeatherDataFromDatabase(context: Context, date: LocalDate) {
        viewModelScope.launch {
            val weatherData = weatherDao.getWeatherDataByDate(date)
            if (weatherData != null) {
                _weatherInfo.value = WeatherInfo(
                    weatherData.date,
                    weatherData.tempMax,
                    weatherData.tempMin,
                    weatherData.location,
                    weatherData.isAverage
                )
            } else {
// Handle missing data in database
                // Display a message indicating data not found in database
                Toast.makeText(
                    context,
                    "No data found for this date. Please try fetching online.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeatherUrl(date: LocalDate): String {
        val today = LocalDate.now().minusDays(1)
        val daysPast = ChronoUnit.DAYS.between(date, today)
        return when {
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun extractTempMax(weatherData: WeatherData?): Double {
        if (weatherData != null) {
            val daysPast =
                ChronoUnit.DAYS.between(_selectedDate.value!!, LocalDate.now().minusDays(1))
            val dateIndex = if (daysPast <= -16) {
                // Find the average for future dates
                weatherData.daily.time.indexOfFirst { it == _selectedDate.value?.toString() }
            } else {
                weatherData.daily.time.indexOf(_selectedDate.value?.toString())
            }
            return if (dateIndex != -1) {
                weatherData.daily.temperature_2m_max[dateIndex]
            } else {
                0.0 // Handle case where date not found in response
            }
        }
        return 0.0 // Handle missing weather data
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun extractTempMin(weatherData: WeatherData?): Double {
        // Similar logic to extractTempMax
        return if (weatherData != null) {
            val daysPast =
                ChronoUnit.DAYS.between(_selectedDate.value!!, LocalDate.now().minusDays(1))
            val dateIndex = if (daysPast <= -16) {
                weatherData.daily.time.indexOfFirst { it == _selectedDate.value?.toString() }
            } else {
                weatherData.daily.time.indexOf(_selectedDate.value?.toString())
            }
            if (dateIndex != -1) {
                weatherData.daily.temperature_2m_min[dateIndex]
            } else {
                0.0
            }
        } else {
            0.0
        }
    }

    private fun extractLocation(weatherData: WeatherData?): String {
        return weatherData?.timezone?.split("/")?.reversed()?.joinToString(", ")?.replace("_", " ")
            ?: ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isFutureAndAverage(date: LocalDate): Boolean {
        val today = LocalDate.now()
        return date.isAfter(today)
    }

    private fun hasInternetConnection(context: Context): Boolean {
        // You can use ConnectivityManager here for more robust checks
        return try {
            val timeoutMs = 1500
            val socket = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)

            socket.connect(socketAddress, timeoutMs)
            socket.close()
            true
        } catch (e: IOException) {
            false
        }
    }
}
