package com.factory.macromindainutritioncoach.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.factory.macromindainutritioncoach.data.local.entity.FoodEntry
import com.factory.macromindainutritioncoach.data.local.entity.UserProfile
import com.factory.macromindainutritioncoach.ui.theme.BreakfastColor
import com.factory.macromindainutritioncoach.ui.theme.CarbsColor
import com.factory.macromindainutritioncoach.ui.theme.DinnerColor
import com.factory.macromindainutritioncoach.ui.theme.FatColor
import com.factory.macromindainutritioncoach.ui.theme.LunchColor
import com.factory.macromindainutritioncoach.ui.theme.ProteinColor
import com.factory.macromindainutritioncoach.ui.theme.SnackColor
import com.factory.macromindainutritioncoach.ui.viewmodel.DashboardViewModel
import com.factory.macromindainutritioncoach.ui.viewmodel.MacroSummary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToAddFood: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val dailyMacros by viewModel.dailyMacros.collectAsStateWithLifecycle()
    val mealBreakdown by viewModel.mealBreakdown.collectAsStateWithLifecycle()

    val today = LocalDate.now()
    val dateLabel = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()) +
            ", " + today.format(DateTimeFormatter.ofPattern("MMM d"))
    val hapticFeedback = LocalHapticFeedback.current

    Scaffold(
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = if (userProfile?.name?.isNotBlank() == true) "Hello, ${userProfile!!.name}!" else "Good day!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (userProfile == null) {
                NewUserSetupCard()
                Spacer(modifier = Modifier.height(20.dp))
            }

            CalorieRingCard(
                consumed = dailyMacros.totalCalories,
                target = userProfile?.targetCalories ?: 2000
            )

            Spacer(modifier = Modifier.height(20.dp))

            MacroProgressCard(macroSummary = dailyMacros, profile = userProfile)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Today's Meals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            listOf("BREAKFAST", "LUNCH", "DINNER", "SNACK").forEach { mealType ->
                val entries = mealBreakdown[mealType] ?: emptyList()
                MealSummaryCard(mealType = mealType, entries = entries)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun NewUserSetupCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Welcome to MacroMind!",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = "Set up your profile to get personalised calorie and macro targets.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun CalorieRingCard(consumed: Int, target: Int) {
    val progress = if (target > 0) (consumed.toFloat() / target).coerceIn(0f, 1f) else 0f
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) progress else 0f,
        animationSpec = tween(durationMillis = 1200),
        label = "calorie_progress"
    )

    LaunchedEffect(Unit) { animationPlayed = true }

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.primaryContainer

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Daily Calories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))

            val remaining = target - consumed
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .semantics(mergeDescendants = true) {
                        contentDescription = "Daily calories: $consumed of $target consumed. " +
                            if (remaining >= 0) "$remaining remaining" else "${-remaining} over target"
                    }
            ) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    val strokeWidth = 20.dp.toPx()
                    val ringSize = size.minDimension - strokeWidth
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                    val arcSize = Size(ringSize, ringSize)
                    drawArc(
                        color = trackColor,
                        startAngle = -230f,
                        sweepAngle = 280f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = primaryColor,
                        startAngle = -230f,
                        sweepAngle = 280f * animatedProgress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$consumed",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "/ $target kcal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (remaining >= 0) "${remaining} remaining" else "${-remaining} over",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (remaining >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun MacroProgressCard(macroSummary: MacroSummary, profile: UserProfile?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Macronutrients",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            MacroProgressRow(
                label = "Protein",
                consumed = macroSummary.totalProtein,
                target = (profile?.targetProtein ?: 150).toFloat(),
                color = ProteinColor
            )
            Spacer(modifier = Modifier.height(10.dp))
            MacroProgressRow(
                label = "Carbs",
                consumed = macroSummary.totalCarbs,
                target = (profile?.targetCarbs ?: 200).toFloat(),
                color = CarbsColor
            )
            Spacer(modifier = Modifier.height(10.dp))
            MacroProgressRow(
                label = "Fat",
                consumed = macroSummary.totalFat,
                target = (profile?.targetFat ?: 65).toFloat(),
                color = FatColor
            )
        }
    }
}

@Composable
fun MacroProgressRow(label: String, consumed: Float, target: Float, color: Color) {
    val progress = if (target > 0) (consumed / target).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 900),
        label = "${label}_progress"
    )
    Column(
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = "$label: ${consumed.toInt()}g of ${target.toInt()}g, ${(progress * 100).toInt()} percent"
        }
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(
                text = "${consumed.toInt()}g / ${target.toInt()}g",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun MealSummaryCard(mealType: String, entries: List<FoodEntry>) {
    val (mealLabel, mealColor) = when (mealType) {
        "BREAKFAST" -> Pair("Breakfast", BreakfastColor)
        "LUNCH" -> Pair("Lunch", LunchColor)
        "DINNER" -> Pair("Dinner", DinnerColor)
        "SNACK" -> Pair("Snacks", SnackColor)
        else -> Pair(mealType, MaterialTheme.colorScheme.primary)
    }
    val totalCalories = entries.sumOf { it.calories }
    val itemCountLabel = when {
        entries.isEmpty() -> "No items logged"
        entries.size == 1 -> "1 item"
        else -> "${entries.size} items"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "$mealLabel: $itemCountLabel" +
                    if (totalCalories > 0) ", $totalCalories calories" else ""
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(mealColor, shape = RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = mealLabel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    text = itemCountLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = if (totalCalories > 0) "$totalCalories kcal" else "—",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (totalCalories > 0) mealColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
