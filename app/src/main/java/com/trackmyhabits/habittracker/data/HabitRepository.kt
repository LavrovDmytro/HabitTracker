package com.trackmyhabits.habittracker.data

import com.trackmyhabits.habittracker.model.Habit
import com.trackmyhabits.habittracker.model.LifeAreaProgress
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {
    suspend fun insertHabit(habit: Habit) {
        habitDao.insert(habit)
    }

    fun getAllHabits(): Flow<List<Habit>> {
        return habitDao.getAll()
    }

    fun getAllLifeAreaProgress(): Flow<List<LifeAreaProgress>> {
        return habitDao.getAllLifeAreaProgress()
    }

    suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit)
    }

    suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit)
    }

    suspend fun incrementPointsForLifeArea(areaName: String, points: Int) {
        val current = habitDao.getLifeArea(areaName)
        val updated = if (current != null) {
            val newPoints = current.points + points
            current.copy(points = newPoints)
        } else {
            LifeAreaProgress(areaName = areaName, points = points)
        }
        habitDao.insertOrUpdateLifeArea(updated)
    }

    suspend fun initializeLifeAreasIfNeeded() {
        val areas = listOf("Development", "Health", "Relationships", "Career", "Leisure")
        areas.forEach { area ->
            val existing = habitDao.getLifeArea(area)
            if (existing == null) {
                habitDao.insertOrUpdateLifeArea(
                    LifeAreaProgress(areaName = area, points = 0)
                )
            }
        }
    }
}