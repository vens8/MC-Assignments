package com.example.axelero.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrientationDataDao {
    @Insert
    suspend fun insert(orientationData: OrientationData)

    @Query("SELECT * FROM orientation_data")
    suspend fun getAllOrientationData(): List<OrientationData>
}