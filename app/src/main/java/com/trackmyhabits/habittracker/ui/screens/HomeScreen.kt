package com.trackmyhabits.habittracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trackmyhabits.habittracker.ui.HabitViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.trackmyhabits.habittracker.model.Habit
import com.trackmyhabits.habittracker.model.ImpactLevel
import com.trackmyhabits.habittracker.model.LifeAreaProgress
import com.trackmyhabits.habittracker.ui.components.EvolvingChick
import com.trackmyhabits.habittracker.ui.components.LifeAreaRow

@Composable
fun HomeScreen(
    onAddHabitClick: () -> Unit,
    onHabitClick: (Habit) -> Unit,
    viewModel: HabitViewModel
) {
    val habits by viewModel.habitList.collectAsState(initial = emptyList())
    val progressList by viewModel.lifeAreaProgress.collectAsState()

    val totalLevel by viewModel.totalLevel.collectAsState()
    val currentChickStage by viewModel.currentChickStage.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("DO", "DONE")

    val filteredHabits = when (selectedTabIndex) {
        0 -> habits.filter { !it.isCompleted }
        1 -> habits.filter { it.isCompleted }
        else -> habits
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddHabitClick) {
                Text(
                    "+",
                    fontSize = 28.sp
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    EvolvingChick(chickStageDrawableResId = currentChickStage.drawableResId)

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("TOTAL LVL", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "$totalLevel",
                            style = MaterialTheme.typography.displaySmall.copy(fontSize = 32.sp)
                        )
                    }
                }


                Column(modifier = Modifier.weight(1f)) {
                    val areaOrder = listOf("Development", "Health", "Relationships", "Career", "Leisure")
                    val areaColors = listOf(
                        Color(0xFF46849B), Color(0xFF8A60B2), Color(0xFFA3B82E), Color(0xFFF08269), Color(0xFFEFCE07)
                    )
                    areaOrder.forEach { name ->
                        val progress = progressList.find { it.areaName == name } ?: LifeAreaProgress(name, 0)
                        val color = areaColors.getOrNull(areaOrder.indexOf(name)) ?: Color.Gray
                        LifeAreaRow(
                            name = progress.areaName,
                            level = progress.level,
                            progress = (progress.points % 10) / 10f,
                            color = color
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            TabRow(selectedTabIndex = selectedTabIndex) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(filteredHabits) { habit ->
                    HabitCard(
                        habit = habit,
                        viewModel = viewModel,
                        isDone = habit.isCompleted,
                        onClick = { onHabitClick(habit) }
                    )
                }
            }
        }

    }
}

@Composable
fun HabitCard(
    habit: Habit,
    viewModel: HabitViewModel,
    isDone: Boolean = false,
    onClick: () -> Unit
) {
    val lifeAreaColors = mapOf(
        "Development" to Color(0xFF46849B),
        "Health" to Color(0xFF8A60B2),
        "Relationships" to Color(0xFFA3B82E),
        "Career" to Color(0xFFF08269),
        "Leisure" to Color(0xFFEFCE07)
    )
    val areaColor = lifeAreaColors[habit.lifeArea] ?: Color.Gray

    val circleSize: Dp = when (habit.impactLevel) {
        ImpactLevel.LITTLE -> 8.dp
        ImpactLevel.LOT -> 12.dp
        ImpactLevel.HUGELY -> 16.dp
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(circleSize)
                        .background(areaColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(20.dp))

                Column {
                    Text(
                        habit.name,
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        habit.lifeArea,
                        style = TextStyle(fontSize = 12.sp)
                    )
                }
            }

            Box(
                modifier = Modifier.requiredSize(48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!isDone) {
                    IconButton(onClick = { viewModel.completeHabit(habit) }, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Mark as done"
                        )
                    }
                } else {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Done"
                    )
                }
            }
        }
    }
}
