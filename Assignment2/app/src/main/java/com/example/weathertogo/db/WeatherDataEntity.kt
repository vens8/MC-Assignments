package com.example.weathertogo.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class WeatherDataEntity(
    @PrimaryKey val date: LocalDate,
    val latitude: Double,
    val longitude: Double,
    val tempMax: Double,
    val tempMin: Double,
    val location: String,
    val isAverage: Boolean
)
