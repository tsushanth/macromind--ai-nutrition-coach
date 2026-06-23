package com.factory.macromindainutritioncoach.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.factory.macromindainutritioncoach.data.local.entity.FoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodEntry(foodEntry: FoodEntry)

    @Delete
    suspend fun deleteFoodEntry(foodEntry: FoodEntry)

    @Query("SELECT * FROM food_entries WHERE date_string = :date ORDER BY timestamp ASC")
    fun getFoodEntriesForDate(date: String): Flow<List<FoodEntry>>

    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC")
    fun getAllFoodEntries(): Flow<List<FoodEntry>>

    @Query("SELECT * FROM food_entries WHERE date_string >= :startDate AND date_string <= :endDate ORDER BY date_string ASC, timestamp ASC")
    fun getFoodEntriesForDateRange(startDate: String, endDate: String): Flow<List<FoodEntry>>

    @Query("SELECT SUM(calories) FROM food_entries WHERE date_string = :date")
    fun getTotalCaloriesForDate(date: String): Flow<Int?>
}
