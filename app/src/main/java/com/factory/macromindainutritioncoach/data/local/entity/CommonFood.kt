package com.factory.macromindainutritioncoach.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "common_foods")
data class CommonFood(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "calories_per_100g") val caloriesPer100g: Int,
    @ColumnInfo(name = "protein_per_100g") val proteinPer100g: Float,
    @ColumnInfo(name = "carbs_per_100g") val carbsPer100g: Float,
    @ColumnInfo(name = "fat_per_100g") val fatPer100g: Float,
    @ColumnInfo(name = "default_serving_size") val defaultServingSize: Float,
    @ColumnInfo(name = "default_serving_unit") val defaultServingUnit: String,
    @ColumnInfo(name = "category") val category: String
)
