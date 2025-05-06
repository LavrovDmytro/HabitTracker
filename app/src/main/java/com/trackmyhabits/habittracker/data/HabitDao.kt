package com.trackmyhabits.habittracker.data

import androidx.room.*
import com.trackmyhabits.habittracker.model.Habit
import com.trackmyhabits.habittracker.model.LifeAreaProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: Habit)

    @Query("SELECT * FROM habits")
    fun getAll(): Flow<List<Habit>>

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("SELECT * FROM life_area_progress WHERE areaName = :areaName LIMIT 1")
    suspend fun getLifeArea(areaName: String): LifeAreaProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLifeArea(lifeAreaProgress: LifeAreaProgress)

    @Query("SELECT * FROM life_area_progress")
    fun getAllLifeAreaProgress(): Flow<List<LifeAreaProgress>>

    @Update
    suspend fun updateHabit(habit: Habit)
}