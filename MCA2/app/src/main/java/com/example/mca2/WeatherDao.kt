package com.example.mca2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weatherEntity: WeatherEntity)

    @Query("SELECT * FROM weather_data WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC LIMIT 10")
    suspend fun getWeatherDataBetweenDates(startDate: Long, endDate: Long): List<WeatherEntity>
}
