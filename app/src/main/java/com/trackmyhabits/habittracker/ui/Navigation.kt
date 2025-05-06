package com.trackmyhabits.habittracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.trackmyhabits.habittracker.ui.screens.AddHabitScreen
import com.trackmyhabits.habittracker.ui.screens.HabitDetailsScreen
import com.trackmyhabits.habittracker.ui.screens.HomeScreen
import com.trackmyhabits.habittracker.ui.screens.SplashScreen

import androidx.navigation.navArgument

@Composable
fun AppNavigation(navController: NavHostController, viewModel: HabitViewModel) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(
                onAddHabitClick = { navController.navigate("addHabit") },
                onHabitClick = { habit -> navController.navigate("habitDetail/${habit.id}") },
                viewModel = viewModel
            )
        }
        composable(
            route = "addHabit?habitId={habitId}",
            arguments = listOf(navArgument("habitId") {
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId")?.toIntOrNull()
            AddHabitScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel,
                habitId = habitId
            )
        }
        composable("habitDetail/{habitId}") { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId")?.toIntOrNull()
            val habitList = viewModel.habitList.collectAsState(initial = emptyList()).value
            val habit = habitList.find { it.id == habitId }

            habit?.let {
                HabitDetailsScreen(
                    habit = it,
                    onBack = { navController.popBackStack() },
                    onDelete = {
                        viewModel.deleteHabit(it)
                        navController.popBackStack()
                    },
                    onEdit = {
                        navController.navigate("addHabit?habitId=${it.id}")
                    },
                )
            }
        }
    }
}
