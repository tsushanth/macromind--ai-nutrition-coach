package com.factory.macromindainutritioncoach.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.factory.macromindainutritioncoach.data.local.dao.CommonFoodDao
import com.factory.macromindainutritioncoach.data.local.dao.FoodEntryDao
import com.factory.macromindainutritioncoach.data.local.dao.UserProfileDao
import com.factory.macromindainutritioncoach.data.local.entity.CommonFood
import com.factory.macromindainutritioncoach.data.local.entity.FoodEntry
import com.factory.macromindainutritioncoach.data.local.entity.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [FoodEntry::class, UserProfile::class, CommonFood::class],
    version = 1,
    exportSchema = false
)
abstract class MacroMindDatabase : RoomDatabase() {

    abstract fun foodEntryDao(): FoodEntryDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun commonFoodDao(): CommonFoodDao

    companion object {
        @Volatile
        private var INSTANCE: MacroMindDatabase? = null

        fun getDatabase(context: Context): MacroMindDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MacroMindDatabase::class.java,
                    "macromind_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // INSTANCE is guaranteed to be set before first DB open (lazy initialization)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateCommonFoods(database.commonFoodDao())
                    }
                }
            }

            suspend fun populateCommonFoods(dao: CommonFoodDao) {
                val foods = arrayOf(
                    // Proteins
                    CommonFood(name = "Chicken Breast", caloriesPer100g = 165, proteinPer100g = 31f, carbsPer100g = 0f, fatPer100g = 3.6f, defaultServingSize = 100f, defaultServingUnit = "g", category = "Protein"),
                    CommonFood(name = "Salmon", caloriesPer100g = 208, proteinPer100g = 20f, carbsPer100g = 0f, fatPer100g = 13f, defaultServingSize = 100f, defaultServingUnit = "g", category = "Protein"),
                    CommonFood(name = "Eggs", caloriesPer100g = 155, proteinPer100g = 13f, carbsPer100g = 1.1f, fatPer100g = 11f, defaultServingSize = 50f, defaultServingUnit = "g", category = "Protein"),
                    CommonFood(name = "Tuna (Canned)", caloriesPer100g = 116, proteinPer100g = 26f, carbsPer100g = 0f, fatPer100g = 1f, defaultServingSize = 85f, defaultServingUnit = "g", category = "Protein"),
                    CommonFood(name = "Turkey Breast", caloriesPer100g = 135, proteinPer100g = 30f, carbsPer100g = 0f, fatPer100g = 1f, defaultServingSize = 100f, defaultServingUnit = "g", category = "Protein"),
                    CommonFood(name = "Greek Yogurt", caloriesPer100g = 59, proteinPer100g = 10f, carbsPer100g = 3.6f, fatPer100g = 0.4f, defaultServingSize = 170f, defaultServingUnit = "g", category = "Dairy"),
                    CommonFood(name = "Cottage Cheese", caloriesPer100g = 98, proteinPer100g = 11f, carbsPer100g = 3.4f, fatPer100g = 4.3f, defaultServingSize = 113f, defaultServingUnit = "g", category = "Dairy"),
                    CommonFood(name = "Beef (Lean Ground)", caloriesPer100g = 215, proteinPer100g = 26f, carbsPer100g = 0f, fatPer100g = 12f, defaultServingSize = 100f, defaultServingUnit = "g", category = "Protein"),
                    CommonFood(name = "Pork Tenderloin", caloriesPer100g = 143, proteinPer100g = 26f, carbsPer100g = 0f, fatPer100g = 3.5f, defaultServingSize = 100f, defaultServingUnit = "g", category = "Protein"),
                    CommonFood(name = "Shrimp", caloriesPer100g = 99, proteinPer100g = 24f, carbsPer100g = 0.2f, fatPer100g = 0.3f, defaultServingSize = 85f, defaultServingUnit = "g", category = "Protein"),
                    CommonFood(name = "Tofu", caloriesPer100g = 76, proteinPer100g = 8f, carbsPer100g = 1.9f, fatPer100g = 4.8f, defaultServingSize = 100f, defaultServingUnit = "g", category = "Protein"),

                    // Grains & Carbs
                    CommonFood(name = "Brown Rice", caloriesPer100g = 216, proteinPer100g = 5f, carbsPer100g = 45f, fatPer100g = 1.8f, defaultServingSize = 195f, defaultServingUnit = "g", category = "Grains"),
                    CommonFood(name = "Oats", caloriesPer100g = 389, proteinPer100g = 17f, carbsPer100g = 66f, fatPer100g = 7f, defaultServingSize = 40f, defaultServingUnit = "g", category = "Grains"),
                    CommonFood(name = "Quinoa", caloriesPer100g = 222, proteinPer100g = 8f, carbsPer100g = 39f, fatPer100g = 3.6f, defaultServingSize = 185f, defaultServingUnit = "g", category = "Grains"),
                    CommonFood(name = "Whole Wheat Bread", caloriesPer100g = 247, proteinPer100g = 13f, carbsPer100g = 41f, fatPer100g = 4.2f, defaultServingSize = 28f, defaultServingUnit = "g", category = "Grains"),
                    CommonFood(name = "Pasta", caloriesPer100g = 371, proteinPer100g = 13f, carbsPer100g = 75f, fatPer100g = 1.5f, defaultServingSize = 85f, defaultServingUnit = "g", category = "Grains"),
                    CommonFood(name = "Sweet Potato", caloriesPer100g = 86, proteinPer100g = 1.6f, carbsPer100g = 20f, fatPer100g = 0.1f, defaultServingSize = 130f, defaultServingUnit = "g", category = "Vegetables"),
                    CommonFood(name = "Black Beans", caloriesPer100g = 132, proteinPer100g = 8.9f, carbsPer100g = 24f, fatPer100g = 0.5f, defaultServingSize = 130f, defaultServingUnit = "g", category = "Legumes"),
                    CommonFood(name = "Lentils", caloriesPer100g = 116, proteinPer100g = 9f, carbsPer100g = 20f, fatPer100g = 0.4f, defaultServingSize = 100f, defaultServingUnit = "g", category = "Legumes"),

                    // Fruits & Vegetables
                    CommonFood(name = "Banana", caloriesPer100g = 89, proteinPer100g = 1.1f, carbsPer100g = 23f, fatPer100g = 0.3f, defaultServingSize = 118f, defaultServingUnit = "g", category = "Fruit"),
                    CommonFood(name = "Apple", caloriesPer100g = 52, proteinPer100g = 0.3f, carbsPer100g = 14f, fatPer100g = 0.2f, defaultServingSize = 182f, defaultServingUnit = "g", category = "Fruit"),
                    CommonFood(name = "Blueberries", caloriesPer100g = 57, proteinPer100g = 0.7f, carbsPer100g = 14f, fatPer100g = 0.3f, defaultServingSize = 148f, defaultServingUnit = "g", category = "Fruit"),
                    CommonFood(name = "Orange", caloriesPer100g = 47, proteinPer100g = 0.9f, carbsPer100g = 12f, fatPer100g = 0.1f, defaultServingSize = 131f, defaultServingUnit = "g", category = "Fruit"),
                    CommonFood(name = "Broccoli", caloriesPer100g = 34, proteinPer100g = 2.8f, carbsPer100g = 7f, fatPer100g = 0.4f, defaultServingSize = 91f, defaultServingUnit = "g", category = "Vegetables"),
                    CommonFood(name = "Spinach", caloriesPer100g = 23, proteinPer100g = 2.9f, carbsPer100g = 3.6f, fatPer100g = 0.4f, defaultServingSize = 30f, defaultServingUnit = "g", category = "Vegetables"),
                    CommonFood(name = "Avocado", caloriesPer100g = 160, proteinPer100g = 2f, carbsPer100g = 9f, fatPer100g = 15f, defaultServingSize = 68f, defaultServingUnit = "g", category = "Fruit"),

                    // Dairy
                    CommonFood(name = "Milk (Whole)", caloriesPer100g = 61, proteinPer100g = 3.2f, carbsPer100g = 4.8f, fatPer100g = 3.3f, defaultServingSize = 244f, defaultServingUnit = "ml", category = "Dairy"),
                    CommonFood(name = "Cheddar Cheese", caloriesPer100g = 402, proteinPer100g = 25f, carbsPer100g = 1.3f, fatPer100g = 33f, defaultServingSize = 28f, defaultServingUnit = "g", category = "Dairy"),

                    // Nuts & Fats
                    CommonFood(name = "Almonds", caloriesPer100g = 579, proteinPer100g = 21f, carbsPer100g = 22f, fatPer100g = 50f, defaultServingSize = 28f, defaultServingUnit = "g", category = "Nuts"),
                    CommonFood(name = "Olive Oil", caloriesPer100g = 884, proteinPer100g = 0f, carbsPer100g = 0f, fatPer100g = 100f, defaultServingSize = 14f, defaultServingUnit = "ml", category = "Fats"),
                    CommonFood(name = "Butter", caloriesPer100g = 717, proteinPer100g = 0.9f, carbsPer100g = 0.1f, fatPer100g = 81f, defaultServingSize = 14f, defaultServingUnit = "g", category = "Fats"),

                    // Fast Food & Indulgences
                    CommonFood(name = "Pizza (Cheese)", caloriesPer100g = 266, proteinPer100g = 11f, carbsPer100g = 33f, fatPer100g = 10f, defaultServingSize = 107f, defaultServingUnit = "g", category = "Fast Food"),
                    CommonFood(name = "Hamburger", caloriesPer100g = 295, proteinPer100g = 17f, carbsPer100g = 24f, fatPer100g = 14f, defaultServingSize = 226f, defaultServingUnit = "g", category = "Fast Food"),
                    CommonFood(name = "French Fries", caloriesPer100g = 312, proteinPer100g = 3.4f, carbsPer100g = 41f, fatPer100g = 15f, defaultServingSize = 117f, defaultServingUnit = "g", category = "Fast Food"),
                    CommonFood(name = "Chocolate (Dark)", caloriesPer100g = 546, proteinPer100g = 5f, carbsPer100g = 60f, fatPer100g = 31f, defaultServingSize = 40f, defaultServingUnit = "g", category = "Sweets"),
                    CommonFood(name = "Ice Cream (Vanilla)", caloriesPer100g = 207, proteinPer100g = 3.5f, carbsPer100g = 24f, fatPer100g = 11f, defaultServingSize = 132f, defaultServingUnit = "g", category = "Sweets"),

                    // Beverages
                    CommonFood(name = "Orange Juice", caloriesPer100g = 45, proteinPer100g = 0.7f, carbsPer100g = 10f, fatPer100g = 0.2f, defaultServingSize = 240f, defaultServingUnit = "ml", category = "Beverages"),
                    CommonFood(name = "Soda (Cola)", caloriesPer100g = 41, proteinPer100g = 0f, carbsPer100g = 11f, fatPer100g = 0f, defaultServingSize = 355f, defaultServingUnit = "ml", category = "Beverages"),
                    CommonFood(name = "Coffee (Black)", caloriesPer100g = 2, proteinPer100g = 0.3f, carbsPer100g = 0f, fatPer100g = 0f, defaultServingSize = 240f, defaultServingUnit = "ml", category = "Beverages"),
                    CommonFood(name = "Tea (Black)", caloriesPer100g = 1, proteinPer100g = 0f, carbsPer100g = 0.3f, fatPer100g = 0f, defaultServingSize = 240f, defaultServingUnit = "ml", category = "Beverages"),

                    // Additional
                    CommonFood(name = "Peanut Butter", caloriesPer100g = 588, proteinPer100g = 25f, carbsPer100g = 20f, fatPer100g = 50f, defaultServingSize = 32f, defaultServingUnit = "g", category = "Nuts"),
                    CommonFood(name = "White Rice (Cooked)", caloriesPer100g = 130, proteinPer100g = 2.7f, carbsPer100g = 28f, fatPer100g = 0.3f, defaultServingSize = 186f, defaultServingUnit = "g", category = "Grains"),
                    CommonFood(name = "Broccoli (Steamed)", caloriesPer100g = 35, proteinPer100g = 2.4f, carbsPer100g = 7.2f, fatPer100g = 0.4f, defaultServingSize = 156f, defaultServingUnit = "g", category = "Vegetables"),
                    CommonFood(name = "Strawberries", caloriesPer100g = 32, proteinPer100g = 0.7f, carbsPer100g = 7.7f, fatPer100g = 0.3f, defaultServingSize = 152f, defaultServingUnit = "g", category = "Fruit"),
                    CommonFood(name = "Walnuts", caloriesPer100g = 654, proteinPer100g = 15f, carbsPer100g = 14f, fatPer100g = 65f, defaultServingSize = 28f, defaultServingUnit = "g", category = "Nuts"),
                    CommonFood(name = "Protein Bar", caloriesPer100g = 380, proteinPer100g = 30f, carbsPer100g = 40f, fatPer100g = 10f, defaultServingSize = 60f, defaultServingUnit = "g", category = "Snacks"),
                    CommonFood(name = "Hummus", caloriesPer100g = 166, proteinPer100g = 7.9f, carbsPer100g = 14f, fatPer100g = 9.6f, defaultServingSize = 100f, defaultServingUnit = "g", category = "Legumes")
                )
                dao.insertAll(*foods)
            }
        }
    }
}
