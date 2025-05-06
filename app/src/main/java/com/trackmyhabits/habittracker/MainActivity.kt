package com.trackmyhabits.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.trackmyhabits.habittracker.ui.theme.HabitTrackerTheme
import androidx.navigation.compose.rememberNavController
import com.trackmyhabits.habittracker.ui.AppNavigation
import androidx.activity.viewModels
import com.trackmyhabits.habittracker.ui.HabitViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: HabitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitTrackerTheme {
                val navController = rememberNavController()
                AppNavigation(navController, viewModel)
            }
        }
    }
}
