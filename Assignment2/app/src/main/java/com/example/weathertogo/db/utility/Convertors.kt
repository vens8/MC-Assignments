package com.example.weathertogo.db.utility

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDate

object Converters {
    @TypeConverter
    @JvmStatic
    fun fromLocalDate(date: LocalDate): String {
        return date.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    @JvmStatic
    fun toLocalDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString)
    }
}