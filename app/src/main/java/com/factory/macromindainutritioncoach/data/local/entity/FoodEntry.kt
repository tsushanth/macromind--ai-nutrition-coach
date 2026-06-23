package com.factory.macromindainutritioncoach.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_entries")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "calories") val calories: Int,
    @ColumnInfo(name = "protein") val protein: Float,
    @ColumnInfo(name = "carbs") val carbs: Float,
    @ColumnInfo(name = "fat") val fat: Float,
    @ColumnInfo(name = "serving_size") val servingSize: Float,
    @ColumnInfo(name = "serving_unit") val servingUnit: String,
    @ColumnInfo(name = "meal_type") val mealType: String, // BREAKFAST, LUNCH, DINNER, SNACK
    @ColumnInfo(name = "date_string") val dateString: String, // yyyy-MM-dd
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)
