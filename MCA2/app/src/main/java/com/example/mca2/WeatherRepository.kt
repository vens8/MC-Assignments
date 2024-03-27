package com.example.mca2

import android.util.Log


class WeatherRepository(
    private val weatherService: WeatherService,
    private val database: WeatherDatabase
) {
    suspend fun insertWeatherData(weatherEntity: WeatherEntity) {
        database.weatherDao().insertWeatherData(weatherEntity)
    }

    suspend fun fetchAndStoreWeatherData(latitude: Double, longitude: Double, date: String, apiKey: String) {
        try {
            val response = weatherService.getWeatherData(latitude, longitude, date, apiKey)

            if (response.isSuccessful) {
                Log.d("response body:", response.body().toString())
                response.body()?.let { weatherData ->
                    // Convert the response into your WeatherEntity format here
                    // Assuming WeatherEntity can directly accept weatherData or its parts
                    val weatherEntity = WeatherEntity(
                        //id = 0, // Assuming AutoGenerate is enabled for ID
                        latitude = latitude,
                        longitude = longitude,
                        date = date,
                        tempMax =weatherData.temperature.max,
                        tempMin = weatherData.temperature.min,
                    )
                    insertWeatherData(weatherEntity)
                }
            } else {
                // Log error or handle unsuccessful response
                response.errorBody()?.string()?.let { Log.d("error fetching weather data:", it) }
                println("Error fetching weather data: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            // Handle exceptions, possibly by logging them
            println("Exception when fetching weather data: $e")
        }
    }
}
