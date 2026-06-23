package com.factory.macromindainutritioncoach.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.factory.macromindainutritioncoach.MacroMindApplication
import com.factory.macromindainutritioncoach.data.local.entity.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileFormState(
    val name: String = "",
    val age: String = "25",
    val weightKg: String = "70",
    val heightCm: String = "170",
    val gender: String = "MALE",
    val activityLevel: String = "MODERATELY_ACTIVE",
    val goal: String = "MAINTAIN"
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as MacroMindApplication).repository

    val userProfile: StateFlow<UserProfile?> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _formState = MutableStateFlow(ProfileFormState())
    val formState: StateFlow<ProfileFormState> = _formState.asStateFlow()

    val isProfileComplete: StateFlow<Boolean> = userProfile
        .map { profile -> profile != null && profile.name.isNotBlank() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val tdee: StateFlow<Int> = _formState
        .map { form -> calculateTDEEFromForm(form) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2000)

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    fun loadFromProfile(profile: UserProfile) {
        _formState.value = ProfileFormState(
            name = profile.name,
            age = profile.age.toString(),
            weightKg = profile.weightKg.toString(),
            heightCm = profile.heightCm.toString(),
            gender = profile.gender,
            activityLevel = profile.activityLevel,
            goal = profile.goal
        )
    }

    fun updateName(value: String) { _formState.value = _formState.value.copy(name = value) }
    fun updateAge(value: String) { _formState.value = _formState.value.copy(age = value) }
    fun updateWeightKg(value: String) { _formState.value = _formState.value.copy(weightKg = value) }
    fun updateHeightCm(value: String) { _formState.value = _formState.value.copy(heightCm = value) }
    fun updateGender(value: String) { _formState.value = _formState.value.copy(gender = value) }
    fun updateActivityLevel(value: String) { _formState.value = _formState.value.copy(activityLevel = value) }
    fun updateGoal(value: String) { _formState.value = _formState.value.copy(goal = value) }

    fun saveProfile() {
        val form = _formState.value
        val age = form.age.toIntOrNull() ?: 25
        val weightKg = form.weightKg.toFloatOrNull() ?: 70f
        val heightCm = form.heightCm.toFloatOrNull() ?: 170f

        val targetCalories = calculateTDEEFromForm(form)
        val macros = repository.calculateMacros(targetCalories, form.goal)

        val profile = UserProfile(
            id = 1,
            name = form.name.trim(),
            age = age,
            weightKg = weightKg,
            heightCm = heightCm,
            gender = form.gender,
            activityLevel = form.activityLevel,
            goal = form.goal,
            targetCalories = targetCalories,
            targetProtein = macros.first,
            targetCarbs = macros.second,
            targetFat = macros.third
        )

        viewModelScope.launch {
            repository.insertOrUpdateProfile(profile)
            _saveSuccess.value = true
        }
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }

    private fun calculateTDEEFromForm(form: ProfileFormState): Int {
        val age = form.age.toIntOrNull() ?: 25
        val weightKg = form.weightKg.toFloatOrNull() ?: 70f
        val heightCm = form.heightCm.toFloatOrNull() ?: 170f

        val tempProfile = UserProfile(
            age = age,
            weightKg = weightKg,
            heightCm = heightCm,
            gender = form.gender,
            activityLevel = form.activityLevel,
            goal = form.goal
        )
        return repository.calculateTDEE(tempProfile)
    }
}
