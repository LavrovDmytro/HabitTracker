package com.trackmyhabits.habittracker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.trackmyhabits.habittracker.data.HabitDatabase
import com.trackmyhabits.habittracker.data.HabitRepository
import com.trackmyhabits.habittracker.model.Habit
import com.trackmyhabits.habittracker.model.ImpactLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.trackmyhabits.habittracker.model.LifeAreaProgress
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.trackmyhabits.habittracker.R

enum class ChickStage(val drawableResId: Int) {
    EGG(R.drawable.egg),
    HATCHING1(R.drawable.chick_hatching1),
    HATCHING2(R.drawable.chick_hatching2),
    FULL_CHICK(R.drawable.chick_full)
}


class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HabitRepository

    val habitList: Flow<List<Habit>>

    init {
        val dao = HabitDatabase.getDatabase(application).habitDao()
        repository = HabitRepository(dao)
        habitList = repository.getAllHabits()
        viewModelScope.launch {
            repository.initializeLifeAreasIfNeeded()
        }
    }

    fun addHabit(name: String, area: String, impactLevel: ImpactLevel) {
        viewModelScope.launch {
            val importance = when (impactLevel) {
                ImpactLevel.LITTLE -> 1
                ImpactLevel.LOT -> 3
                ImpactLevel.HUGELY -> 5
            }

            val newHabit = Habit(name = name, lifeArea = area, impactLevel = impactLevel, importance = importance)
            repository.insertHabit(newHabit)
        }
    }

    fun updateHabit(updatedHabit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(updatedHabit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun completeHabit(habit: Habit) {
        viewModelScope.launch {
            val updated = habit.copy(isCompleted = true)
            repository.insertHabit(updated)

            val pointsToAdd = when (habit.impactLevel) {
                ImpactLevel.LITTLE -> 1
                ImpactLevel.LOT -> 3
                ImpactLevel.HUGELY -> 5
            }

            repository.incrementPointsForLifeArea(habit.lifeArea, pointsToAdd)
        }
    }

    val lifeAreaProgress: StateFlow<List<LifeAreaProgress>> =
        repository.getAllLifeAreaProgress().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val totalLevel: StateFlow<Int> = lifeAreaProgress
        .map { progressList ->
            progressList.sumOf { it.level }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    val currentChickStage: StateFlow<ChickStage> = totalLevel
        .map { level ->
            when {
                level >= 3 -> ChickStage.FULL_CHICK
                level >= 2 -> ChickStage.HATCHING2
                level >= 1 -> ChickStage.HATCHING1
                else -> ChickStage.EGG
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ChickStage.EGG
        )
}
