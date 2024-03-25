package com.example.weathertogo.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.time.LocalDate

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weatherData: WeatherDataEntity)

    @Query("SELECT * FROM weather_data WHERE date = :date AND latitude = :latitude AND longitude = :longitude")
    suspend fun getWeatherDataByDateAndCoordinates(latitude: Double, longitude: Double, date: LocalDate): WeatherDataEntity?
}
