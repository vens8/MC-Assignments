package com.example.axelero.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [OrientationData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orientationDataDao(): OrientationDataDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "orientation-database"
                ).build().also { instance = it }
            }
        }
    }
}