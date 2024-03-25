package com.example.weathertogo.viewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertogo.db.WeatherDataEntity
import com.example.weathertogo.db.WeatherDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    val messages = MutableLiveData<List<String>>(emptyList())


    fun setSelectedLatitude(lat: String) {
        try {
            val latitude = lat.toDouble()
            // Validate for valid latitude ranges (optional)
            _selectedLatitude.value = latitude
        } catch (e: NumberFormatException) {
            // Handle non-numeric input error
            Log.e("WeatherViewModel", "Invalid latitude input")
        }
    }

    fun setSelectedLongitude(lon: String) {
        try {
            val longitude = lon.toDouble()
            // Validate for valid latitude ranges (optional)
            _selectedLongitude.value = longitude
        } catch (e: NumberFormatException) {
            // Handle non-numeric input error
            Log.e("WeatherViewModel", "Invalid longitude input")
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
                // Make API call and store data in database
                viewModelScope.launch {
                    val hasConnection = hasInternetConnection()
                    if (hasConnection) {
                        Log.d("WeatherViewModel", "Internet connection available")
                        try {
                            val retrofit = Retrofit.Builder()
                                .baseUrl("https://api.open-meteo.com/v1/") // Base URL for both forecast and archive APIs
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()

                            val weatherService = retrofit.create(WeatherService::class.java)
                            Log.d("WeatherViewModel", "API call started")
                            val url = getWeatherUrl(date)
                            Log.d("WeatherViewModel", "URL: $url")
                            val response = weatherService.getWeatherData(url) // Call the appropriate API based on date

                            if (response.isSuccessful) {
                                Log.d("WeatherViewModel", "API call successful")
                                val weatherData = response.body()
                                _weatherData.value = weatherData
                                if (weatherData != null) {
                                    val today = LocalDate.now()
                                    val daysPast = ChronoUnit.DAYS.between(date, today)
                                    Log.d("DaysPast", "$daysPast")
                                    if (daysPast <= -16) {
                                        val tempMaxAverage = String.format("%.2f", weatherData.daily.temperature_2m_max.filterNotNull().average()).toDouble()
                                        val tempMinAverage = String.format("%.2f", weatherData.daily.temperature_2m_min.filterNotNull().average()).toDouble()
                                        val location = weatherData.timezone.split("/").reversed().joinToString(", ").replace("_", " ")
                                        _weatherInfo.value = WeatherInfo(date, _selectedLatitude.value!!, _selectedLongitude.value!!, tempMaxAverage, tempMinAverage, location, true)
                                        insertWeatherDataToDatabase(_weatherInfo.value!!)
                                        val newMessages = messages.value?.toMutableList()
                                        newMessages?.add("You're online!\nWe've fetched the report from online and saved it to the database for future offline access :)")
                                        messages.value = newMessages!!
                                    }
                                    else {
                                        val dateIndex = weatherData.daily.time.indexOf(date.toString())
                                        if (dateIndex != -1) {
                                            val tempMax = weatherData.daily.temperature_2m_max[dateIndex]
                                            val tempMin = weatherData.daily.temperature_2m_min[dateIndex]
                                            val location = weatherData.timezone.split("/").reversed().joinToString(", ").replace("_", " ")
                                            _weatherInfo.value = WeatherInfo(date, _selectedLatitude.value!!, _selectedLongitude.value!!, tempMax, tempMin, location)
                                            insertWeatherDataToDatabase(_weatherInfo.value!!)
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
                    } else {
                        // Fetch data from database
                        Log.d("WeatherViewModel", "No internet connection")
                        val newMessages = messages.value?.toMutableList()
                        newMessages?.add("You're offline :(\nSearching database...")
                        messages.value = newMessages!!
                        fetchWeatherDataFromDatabase(_selectedLatitude.value!!, _selectedLongitude.value!!, date)
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

    private fun insertWeatherDataToDatabase(weatherInfo: WeatherInfo) {
        // use viewModelScope here??
        viewModelScope.launch {
            try {
                val weatherDataEntity = WeatherDataEntity(
                    weatherInfo.date,
                    weatherInfo.latitude,
                    weatherInfo.longitude,
                    weatherInfo.tempMax,
                    weatherInfo.tempMin,
                    weatherInfo.location,
                    weatherInfo.isAverage,
                )
                weatherDao.insertWeatherData(weatherDataEntity)
            } catch (e: Exception) {
                // Handle database errors
                val newMessages = messages.value?.toMutableList()
                newMessages?.add("Database error: ${e.message}")
                messages.value = newMessages!!
                Log.e("WeatherViewModel", "Database error: ${e.message}")
            }
        }
    }

    private fun fetchWeatherDataFromDatabase(lat: Double, lon: Double, date: LocalDate) {
        viewModelScope.launch {
            Log.d("WeatherViewModel", "Fetching data from database")
            val weatherData = weatherDao.getWeatherDataByDateAndCoordinates(lat, lon, date)
            if (weatherData != null) {
                Log.d("tempMax", "${weatherData.tempMax}")
                _weatherInfo.value = WeatherInfo(
                    weatherData.date,
                    weatherData.latitude,
                    weatherData.longitude,
                    weatherData.tempMax,
                    weatherData.tempMin,
                    weatherData.location,
                    weatherData.isAverage,
                    true
                )
            } else {
                // Handle missing data in database
                // Display a message indicating data not found in database
                val newMessages = messages.value?.toMutableList()
                newMessages?.add("No data found for this date and coordinates in the database. Please try fetching again when online.")
                messages.value = newMessages!!
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

    private suspend fun hasInternetConnection(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
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
}
