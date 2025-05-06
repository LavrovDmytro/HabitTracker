package com.trackmyhabits.habittracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "life_area_progress")
data class LifeAreaProgress(
    @PrimaryKey val areaName: String,
    val points: Int = 0
) {
    val level: Int
        get() = points / 10
}
