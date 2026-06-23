package com.factory.macromindainutritioncoach.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.factory.macromindainutritioncoach.data.local.entity.FoodEntry
import com.factory.macromindainutritioncoach.ui.theme.BreakfastColor
import com.factory.macromindainutritioncoach.ui.theme.DinnerColor
import com.factory.macromindainutritioncoach.ui.theme.LunchColor
import com.factory.macromindainutritioncoach.ui.theme.SnackColor
import com.factory.macromindainutritioncoach.ui.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodLogScreen(
    viewModel: DashboardViewModel,
    onNavigateToAddFood: () -> Unit
) {
    val todayEntries by viewModel.todayEntries.collectAsStateWithLifecycle()
    val mealBreakdown by viewModel.mealBreakdown.collectAsStateWithLifecycle()

    val today = LocalDate.now()
    val dateLabel = today.format(DateTimeFormatter.ofPattern("EEEE, MMM d"))
    val hapticFeedback = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Food Log", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(dateLabel, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onNavigateToAddFood()
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add food entry")
            }
        }
    ) { paddingValues ->
        if (todayEntries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No food logged today", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap + to start logging your meals",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                listOf("BREAKFAST", "LUNCH", "DINNER", "SNACK").forEach { mealType ->
                    val entries = mealBreakdown[mealType] ?: emptyList()
                    item {
                        MealSection(
                            mealType = mealType,
                            entries = entries,
                            onDeleteEntry = { viewModel.deleteEntry(it) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealSection(
    mealType: String,
    entries: List<FoodEntry>,
    onDeleteEntry: (FoodEntry) -> Unit
) {
    val (mealLabel, mealColor) = when (mealType) {
        "BREAKFAST" -> Pair("Breakfast", BreakfastColor)
        "LUNCH" -> Pair("Lunch", LunchColor)
        "DINNER" -> Pair("Dinner", DinnerColor)
        "SNACK" -> Pair("Snacks", SnackColor)
        else -> Pair(mealType, MaterialTheme.colorScheme.primary)
    }

    val hapticFeedback = LocalHapticFeedback.current
    var expanded by remember { mutableStateOf(true) }
    val totalCalories = entries.sumOf { it.calories }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(mealColor, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = mealLabel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$totalCalories kcal",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = mealColor
                )
                IconButton(onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    expanded = !expanded
                }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse $mealLabel" else "Expand $mealLabel"
                    )
                }
            }

            if (expanded) {
                if (entries.isEmpty()) {
                    Text(
                        text = "No items logged for $mealLabel",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                } else {
                    entries.forEach { entry ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    onDeleteEntry(entry)
                                    true
                                } else false
                            }
                        )
                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.errorContainer)
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        ) {
                            FoodEntryRow(entry = entry)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun FoodEntryRow(entry: FoodEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "${entry.name}, ${entry.servingSize.toInt()}${entry.servingUnit}, " +
                    "protein ${entry.protein.toInt()}g, carbs ${entry.carbs.toInt()}g, " +
                    "fat ${entry.fat.toInt()}g, ${entry.calories} calories. Swipe left to delete."
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${entry.servingSize.toInt()}${entry.servingUnit} • " +
                        "P:${entry.protein.toInt()}g C:${entry.carbs.toInt()}g F:${entry.fat.toInt()}g",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "${entry.calories} kcal",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
