package com.factory.macromindainutritioncoach.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.factory.macromindainutritioncoach.data.local.entity.CommonFood
import kotlinx.coroutines.flow.Flow

@Dao
interface CommonFoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg foods: CommonFood)

    @Query("SELECT * FROM common_foods WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchFoods(query: String): Flow<List<CommonFood>>

    @Query("SELECT * FROM common_foods ORDER BY name ASC")
    fun getAllFoods(): Flow<List<CommonFood>>

    @Query("SELECT * FROM common_foods WHERE category = :category ORDER BY name ASC")
    fun getFoodsByCategory(category: String): Flow<List<CommonFood>>

    @Query("SELECT COUNT(*) FROM common_foods")
    suspend fun getCount(): Int
}
