package com.factory.macromindainutritioncoach.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int = 1,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "age") val age: Int = 25,
    @ColumnInfo(name = "weight_kg") val weightKg: Float = 70f,
    @ColumnInfo(name = "height_cm") val heightCm: Float = 170f,
    @ColumnInfo(name = "gender") val gender: String = "MALE", // MALE, FEMALE
    @ColumnInfo(name = "activity_level") val activityLevel: String = "MODERATELY_ACTIVE",
    // SEDENTARY, LIGHTLY_ACTIVE, MODERATELY_ACTIVE, VERY_ACTIVE, EXTRA_ACTIVE
    @ColumnInfo(name = "goal") val goal: String = "MAINTAIN", // LOSE, MAINTAIN, GAIN
    @ColumnInfo(name = "target_calories") val targetCalories: Int = 2000,
    @ColumnInfo(name = "target_protein") val targetProtein: Int = 150,
    @ColumnInfo(name = "target_carbs") val targetCarbs: Int = 200,
    @ColumnInfo(name = "target_fat") val targetFat: Int = 65
)
