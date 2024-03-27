package com.example.mca2

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherData(
    val lat: Double,
    val lon: Double,
    val tz: String,
    val date: String,
    val units: String,
    val cloud_cover: CloudCover,
    val humidity: Humidity,
    val precipitation: Precipitation,
    val temperature: Temperature,
    val pressure: Pressure,
    val wind: Wind
)

data class CloudCover(
    val afternoon: Int
)

data class Humidity(
    val afternoon: Int
)

data class Precipitation(
    val total: Int
)

data class Temperature(
    val min: Double,
    val max: Double,
    val afternoon: Double,
    val night: Double,
    val evening: Double,
    val morning: Double
)

data class Pressure(
    val afternoon: Int
)

data class Wind(
    val max: WindMax
)

data class WindMax(
    val speed: Double,
    val direction: Int
)



interface WeatherService {
    @GET("data/3.0/onecall/day_summary")
    fun getWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("date") date: String, // Date in Unix timestamp format
        @Query("appid") apiKey: String
    ): Call<WeatherData>
}
class WeatherViewModel(context: Context) : ViewModel() {

    private val repository: WeatherRepository

    // LiveData for UI update
    private val weatherDataState = MutableLiveData<String>()
    //    private val weatherDatabase = WeatherDatabase.getDatabase(application)
    private val weatherDatabase = WeatherDatabase.getDatabase(context)

    init {
        // Assuming WeatherDatabase is accessible via App class as shown in previous examples

        // Initialize Retrofit and WeatherService
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherService = retrofit.create(WeatherService::class.java)

        // Initialize repository with database and network service
        repository = WeatherRepository(weatherService, weatherDatabase)
    }


    fun fetchAndStoreWeatherData(latitude: Double, longitude: Double, date: String) {
        viewModelScope.launch {
            try {
                Log.d("received lat and long:", "$latitude, $longitude, $date")
                val apiKey = "5ed2e3ffc42a36de4e2ff129e063b5ce"
                repository.fetchAndStoreWeatherData(latitude, longitude, date, apiKey)
                // Update your LiveData or state based on successful data fetch and store
                weatherDataState.postValue("Weather data fetched and stored successfully")
            } catch (e: Exception) {
                // Handle any errors, including updating the UI via LiveData
                weatherDataState.postValue("Failed to fetch weather data: ${e.message}")
            }
        }
    }


}


