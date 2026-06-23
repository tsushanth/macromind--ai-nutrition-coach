package com.factory.macromindainutritioncoach.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.factory.macromindainutritioncoach.MacroMindApplication
import com.factory.macromindainutritioncoach.data.local.entity.FoodEntry
import com.factory.macromindainutritioncoach.data.local.entity.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class MacroSummary(
    val totalCalories: Int = 0,
    val totalProtein: Float = 0f,
    val totalCarbs: Float = 0f,
    val totalFat: Float = 0f
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as MacroMindApplication).repository
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val today: String get() = LocalDate.now().format(dateFormatter)

    val userProfile: StateFlow<UserProfile?> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayEntries: StateFlow<List<FoodEntry>> = repository.getFoodEntriesForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyMacros: StateFlow<MacroSummary> = todayEntries
        .map { entries ->
            MacroSummary(
                totalCalories = entries.sumOf { it.calories },
                totalProtein = entries.sumOf { it.protein.toDouble() }.toFloat(),
                totalCarbs = entries.sumOf { it.carbs.toDouble() }.toFloat(),
                totalFat = entries.sumOf { it.fat.toDouble() }.toFloat()
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MacroSummary())

    val mealBreakdown: StateFlow<Map<String, List<FoodEntry>>> = todayEntries
        .map { entries ->
            val result = mutableMapOf<String, MutableList<FoodEntry>>()
            listOf("BREAKFAST", "LUNCH", "DINNER", "SNACK").forEach { meal ->
                result[meal] = mutableListOf()
            }
            entries.forEach { entry ->
                result.getOrPut(entry.mealType) { mutableListOf() }.add(entry)
            }
            result.mapValues { it.value.toList() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun deleteEntry(entry: FoodEntry) {
        viewModelScope.launch {
            repository.deleteFoodEntry(entry)
        }
    }
}
