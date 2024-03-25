package com.example.weathertogo.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "weather_data", primaryKeys = ["date", "latitude", "longitude"])
data class WeatherDataEntity(
    val date: LocalDate,
    val latitude: Double,
    val longitude: Double,
    val tempMax: Double,
    val tempMin: Double,
    val location: String,
    val isAverage: Boolean,
)
