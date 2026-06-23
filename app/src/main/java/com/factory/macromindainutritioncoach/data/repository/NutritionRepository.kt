package com.factory.macromindainutritioncoach.data.repository

import com.factory.macromindainutritioncoach.data.local.dao.CommonFoodDao
import com.factory.macromindainutritioncoach.data.local.dao.FoodEntryDao
import com.factory.macromindainutritioncoach.data.local.dao.UserProfileDao
import com.factory.macromindainutritioncoach.data.local.entity.CommonFood
import com.factory.macromindainutritioncoach.data.local.entity.FoodEntry
import com.factory.macromindainutritioncoach.data.local.entity.UserProfile
import kotlinx.coroutines.flow.Flow

class NutritionRepository(
    private val foodEntryDao: FoodEntryDao,
    private val userProfileDao: UserProfileDao,
    private val commonFoodDao: CommonFoodDao
) {

    // Food Entry operations
    suspend fun insertFoodEntry(foodEntry: FoodEntry) {
        foodEntryDao.insertFoodEntry(foodEntry)
    }

    suspend fun deleteFoodEntry(foodEntry: FoodEntry) {
        foodEntryDao.deleteFoodEntry(foodEntry)
    }

    fun getFoodEntriesForDate(date: String): Flow<List<FoodEntry>> {
        return foodEntryDao.getFoodEntriesForDate(date)
    }

    fun getAllFoodEntries(): Flow<List<FoodEntry>> {
        return foodEntryDao.getAllFoodEntries()
    }

    fun getFoodEntriesForDateRange(startDate: String, endDate: String): Flow<List<FoodEntry>> {
        return foodEntryDao.getFoodEntriesForDateRange(startDate, endDate)
    }

    fun getTotalCaloriesForDate(date: String): Flow<Int?> {
        return foodEntryDao.getTotalCaloriesForDate(date)
    }

    // User Profile operations
    suspend fun insertOrUpdateProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdate(profile)
    }

    fun getUserProfile(): Flow<UserProfile?> {
        return userProfileDao.getUserProfile()
    }

    suspend fun getUserProfileOnce(): UserProfile? {
        return userProfileDao.getUserProfileOnce()
    }

    // Common Food operations
    fun searchFoods(query: String): Flow<List<CommonFood>> {
        return commonFoodDao.searchFoods(query)
    }

    fun getAllFoods(): Flow<List<CommonFood>> {
        return commonFoodDao.getAllFoods()
    }

    fun getFoodsByCategory(category: String): Flow<List<CommonFood>> {
        return commonFoodDao.getFoodsByCategory(category)
    }

    /**
     * Calculate Total Daily Energy Expenditure (TDEE) using Mifflin-St Jeor equation.
     * Returns calories per day based on profile settings.
     */
    fun calculateTDEE(profile: UserProfile): Int {
        // Mifflin-St Jeor BMR
        val bmr = if (profile.gender == "MALE") {
            (10 * profile.weightKg) + (6.25f * profile.heightCm) - (5 * profile.age) + 5
        } else {
            (10 * profile.weightKg) + (6.25f * profile.heightCm) - (5 * profile.age) - 161
        }

        val activityMultiplier = when (profile.activityLevel) {
            "SEDENTARY" -> 1.2f
            "LIGHTLY_ACTIVE" -> 1.375f
            "MODERATELY_ACTIVE" -> 1.55f
            "VERY_ACTIVE" -> 1.725f
            "EXTRA_ACTIVE" -> 1.9f
            else -> 1.55f
        }

        val tdee = (bmr * activityMultiplier).toInt()

        return when (profile.goal) {
            "LOSE" -> (tdee - 500).coerceAtLeast(1200)
            "GAIN" -> tdee + 300
            else -> tdee // MAINTAIN
        }
    }

    /**
     * Calculate macro targets in grams based on calorie goal and fitness goal.
     * Returns Triple(proteinG, carbsG, fatG)
     */
    fun calculateMacros(calories: Int, goal: String): Triple<Int, Int, Int> {
        return when (goal) {
            "LOSE" -> {
                // Higher protein to preserve muscle during cut
                val protein = (calories * 0.35f / 4).toInt()
                val fat = (calories * 0.30f / 9).toInt()
                val carbs = (calories * 0.35f / 4).toInt()
                Triple(protein, carbs, fat)
            }
            "GAIN" -> {
                // Higher carbs for energy surplus
                val protein = (calories * 0.30f / 4).toInt()
                val fat = (calories * 0.25f / 9).toInt()
                val carbs = (calories * 0.45f / 4).toInt()
                Triple(protein, carbs, fat)
            }
            else -> {
                // Balanced maintenance
                val protein = (calories * 0.30f / 4).toInt()
                val fat = (calories * 0.30f / 9).toInt()
                val carbs = (calories * 0.40f / 4).toInt()
                Triple(protein, carbs, fat)
            }
        }
    }
}
