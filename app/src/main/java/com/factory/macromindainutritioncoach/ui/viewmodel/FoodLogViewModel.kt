package com.factory.macromindainutritioncoach.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.factory.macromindainutritioncoach.MacroMindApplication
import com.factory.macromindainutritioncoach.data.local.entity.CommonFood
import com.factory.macromindainutritioncoach.data.local.entity.FoodEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FoodFormState(
    val name: String = "",
    val calories: String = "",
    val protein: String = "",
    val carbs: String = "",
    val fat: String = "",
    val servingSize: String = "100",
    val servingUnit: String = "g",
    val mealType: String = "BREAKFAST"
)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class FoodLogViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as MacroMindApplication).repository
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMealType = MutableStateFlow("BREAKFAST")
    val selectedMealType: StateFlow<String> = _selectedMealType.asStateFlow()

    private val _formState = MutableStateFlow(FoodFormState())
    val formState: StateFlow<FoodFormState> = _formState.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    val searchResults: StateFlow<List<CommonFood>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllFoods()
            } else {
                repository.searchFoods(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateMealType(mealType: String) {
        _selectedMealType.value = mealType
        _formState.value = _formState.value.copy(mealType = mealType)
    }

    fun updateFormField(field: String, value: String) {
        _formState.value = when (field) {
            "name" -> _formState.value.copy(name = value)
            "calories" -> _formState.value.copy(calories = value)
            "protein" -> _formState.value.copy(protein = value)
            "carbs" -> _formState.value.copy(carbs = value)
            "fat" -> _formState.value.copy(fat = value)
            "servingSize" -> _formState.value.copy(servingSize = value)
            "servingUnit" -> _formState.value.copy(servingUnit = value)
            else -> _formState.value
        }
    }

    fun selectCommonFood(food: CommonFood, servingSize: Float) {
        val factor = servingSize / 100f
        _formState.value = FoodFormState(
            name = food.name,
            calories = (food.caloriesPer100g * factor).toInt().toString(),
            protein = String.format("%.1f", food.proteinPer100g * factor),
            carbs = String.format("%.1f", food.carbsPer100g * factor),
            fat = String.format("%.1f", food.fatPer100g * factor),
            servingSize = servingSize.toString(),
            servingUnit = food.defaultServingUnit,
            mealType = _selectedMealType.value
        )
    }

    fun saveFoodEntry(): Boolean {
        val form = _formState.value
        if (form.name.isBlank()) return false
        val calories = form.calories.toIntOrNull() ?: return false
        val protein = form.protein.toFloatOrNull() ?: 0f
        val carbs = form.carbs.toFloatOrNull() ?: 0f
        val fat = form.fat.toFloatOrNull() ?: 0f
        val servingSize = form.servingSize.toFloatOrNull() ?: 100f

        val today = LocalDate.now().format(dateFormatter)
        val entry = FoodEntry(
            name = form.name.trim(),
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            servingSize = servingSize,
            servingUnit = form.servingUnit.ifBlank { "g" },
            mealType = form.mealType.ifBlank { _selectedMealType.value },
            dateString = today
        )

        viewModelScope.launch {
            repository.insertFoodEntry(entry)
            _saveSuccess.value = true
            resetForm()
        }
        return true
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }

    private fun resetForm() {
        _formState.value = FoodFormState(mealType = _selectedMealType.value)
        _searchQuery.value = ""
    }
}
