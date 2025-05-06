package com.trackmyhabits.habittracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ImpactLevel { LITTLE, LOT, HUGELY }

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val lifeArea: String,
    val isCompleted: Boolean = false,
    val impactLevel: ImpactLevel = ImpactLevel.LITTLE,
    val importance: Int
)