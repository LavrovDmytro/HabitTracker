package com.trackmyhabits.habittracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trackmyhabits.habittracker.ui.HabitViewModel
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import com.trackmyhabits.habittracker.model.Habit
import com.trackmyhabits.habittracker.model.ImpactLevel

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddHabitScreen(
    onBack: () -> Unit,
    viewModel: HabitViewModel,
    habitId: Int? = null
) {
    var habitToEdit by remember { mutableStateOf<Habit?>(null) }

    LaunchedEffect(habitId) {
        if (habitId != null) {
            viewModel.habitList.collect { habits ->
                habitToEdit = habits.find { it.id == habitId }
            }
        } else {
            habitToEdit = null
        }
    }

    var text by remember(habitToEdit) { mutableStateOf(habitToEdit?.name ?: "") }
    val areas = listOf("Development", "Health", "Relationships", "Career", "Leisure")
    var selectedArea by remember(habitToEdit) { mutableStateOf(habitToEdit?.lifeArea ?: areas[0]) }
    val impactLevels = listOf("A little", "A lot", "Hugely")
    val initialImpact = remember(habitToEdit) {
        when (habitToEdit?.impactLevel) {
            ImpactLevel.LITTLE -> "A little"
            ImpactLevel.LOT -> "A lot"
            ImpactLevel.HUGELY -> "Hugely"
            else -> impactLevels[0]
        }
    }
    var selectedImpact by remember(habitToEdit) { mutableStateOf(initialImpact) }

    val impactEnum = when (selectedImpact) {
        "A little" -> ImpactLevel.LITTLE
        "A lot" -> ImpactLevel.LOT
        "Hugely" -> ImpactLevel.HUGELY
        else -> ImpactLevel.LITTLE
    }

    val lifeAreaColors = mapOf(
        "Development" to Color(0xFF46849B),
        "Health" to Color(0xFF8A60B2),
        "Relationships" to Color(0xFFA3B82E),
        "Career" to Color(0xFFF08269),
        "Leisure" to Color(0xFFEFCE07)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if (habitToEdit != null) "Edit a habit" else "New Habit")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 0.dp)
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Habit title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Life sphere to be affected", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    areas.forEach { areaName ->
                        val isSelected = selectedArea == areaName
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedArea = areaName },
                            label = { Text(areaName) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(lifeAreaColors[areaName] ?: Color.Gray, CircleShape)
                                )
                            },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))


                Text("Achieving this will improve my life sphere...", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    impactLevels.forEach { impactText ->
                        val isSelected = selectedImpact == impactText
                        val dotSize = when (impactText) {
                            "A little" -> 8.dp
                            "A lot" -> 12.dp
                            "Hugely" -> 16.dp
                            else -> 8.dp
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedImpact = impactText },
                            label = { Text(impactText) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(dotSize)
                                        .background(Color.Gray, CircleShape)
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        val currentHabitToEdit = habitToEdit
                        if (currentHabitToEdit != null) {
                            viewModel.updateHabit(
                                currentHabitToEdit.copy(
                                    name = text,
                                    lifeArea = selectedArea,
                                    impactLevel = impactEnum,
                                    importance = when (impactEnum) {
                                        ImpactLevel.LITTLE -> 1
                                        ImpactLevel.LOT -> 3
                                        ImpactLevel.HUGELY -> 5
                                    }
                                )
                            )
                        } else {
                            viewModel.addHabit(text, selectedArea, impactEnum)
                        }
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (habitToEdit != null) "Save" else "Create")
            }
        }
    }
}
