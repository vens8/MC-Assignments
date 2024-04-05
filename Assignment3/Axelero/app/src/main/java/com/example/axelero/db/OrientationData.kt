package com.example.axelero.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orientation_data")
data class OrientationData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val xAngle: Float,
    val yAngle: Float,
    val zAngle: Float,
    val timestamp: Long
)