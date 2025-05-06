package com.trackmyhabits.habittracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.trackmyhabits.habittracker.model.Habit
import com.trackmyhabits.habittracker.model.LifeAreaProgress

@Database(entities = [Habit::class, LifeAreaProgress::class], version = 2)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
