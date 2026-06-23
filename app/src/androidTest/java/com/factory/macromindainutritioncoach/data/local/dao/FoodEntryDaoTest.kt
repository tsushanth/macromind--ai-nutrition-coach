package com.factory.macromindainutritioncoach.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.factory.macromindainutritioncoach.data.local.database.MacroMindDatabase
import com.factory.macromindainutritioncoach.data.local.entity.FoodEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class FoodEntryDaoTest {

    private lateinit var database: MacroMindDatabase
    private lateinit var dao: FoodEntryDao

    private fun makeEntry(
        name: String = "Chicken",
        calories: Int = 200,
        protein: Float = 30f,
        carbs: Float = 0f,
        fat: Float = 5f,
        mealType: String = "LUNCH",
        dateString: String = "2024-01-15"
    ) = FoodEntry(
        name = name,
        calories = calories,
        protein = protein,
        carbs = carbs,
        fat = fat,
        servingSize = 100f,
        servingUnit = "g",
        mealType = mealType,
        dateString = dateString
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MacroMindDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.foodEntryDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    // --- insertFoodEntry & getFoodEntriesForDate ---

    @Test
    fun insertAndRetrieveFoodEntryByDate() = runTest {
        val entry = makeEntry(name = "Apple", calories = 95, dateString = "2024-01-15")
        dao.insertFoodEntry(entry)

        dao.getFoodEntriesForDate("2024-01-15").test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("Apple", items[0].name)
            assertEquals(95, items[0].calories)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFoodEntriesForDate_returnsOnlyMatchingDate() = runTest {
        dao.insertFoodEntry(makeEntry(name = "Breakfast Item", dateString = "2024-01-15"))
        dao.insertFoodEntry(makeEntry(name = "Other Day Item", dateString = "2024-01-16"))

        dao.getFoodEntriesForDate("2024-01-15").test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("Breakfast Item", items[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFoodEntriesForDate_returnsEmptyListWhenNoneForDate() = runTest {
        dao.insertFoodEntry(makeEntry(dateString = "2024-01-15"))

        dao.getFoodEntriesForDate("2024-01-20").test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertMultipleEntriesForSameDate() = runTest {
        dao.insertFoodEntry(makeEntry(name = "Breakfast", mealType = "BREAKFAST", dateString = "2024-02-01"))
        dao.insertFoodEntry(makeEntry(name = "Lunch", mealType = "LUNCH", dateString = "2024-02-01"))
        dao.insertFoodEntry(makeEntry(name = "Dinner", mealType = "DINNER", dateString = "2024-02-01"))

        dao.getFoodEntriesForDate("2024-02-01").test {
            val items = awaitItem()
            assertEquals(3, items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- deleteFoodEntry ---

    @Test
    fun deleteFoodEntry_removesItFromDatabase() = runTest {
        val entry = makeEntry(name = "Deletable", dateString = "2024-01-15")
        dao.insertFoodEntry(entry)

        // Get the inserted entry with its generated ID
        var inserted: FoodEntry? = null
        dao.getFoodEntriesForDate("2024-01-15").test {
            inserted = awaitItem().first()
            cancelAndIgnoreRemainingEvents()
        }

        dao.deleteFoodEntry(inserted!!)

        dao.getFoodEntriesForDate("2024-01-15").test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteOneEntry_leavesOthersIntact() = runTest {
        dao.insertFoodEntry(makeEntry(name = "Keep Me", dateString = "2024-01-15"))
        dao.insertFoodEntry(makeEntry(name = "Delete Me", dateString = "2024-01-15"))

        var toDelete: FoodEntry? = null
        dao.getFoodEntriesForDate("2024-01-15").test {
            toDelete = awaitItem().first { it.name == "Delete Me" }
            cancelAndIgnoreRemainingEvents()
        }

        dao.deleteFoodEntry(toDelete!!)

        dao.getFoodEntriesForDate("2024-01-15").test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("Keep Me", items[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- getAllFoodEntries ---

    @Test
    fun getAllFoodEntries_returnsAllAcrossDates() = runTest {
        dao.insertFoodEntry(makeEntry(dateString = "2024-01-10"))
        dao.insertFoodEntry(makeEntry(dateString = "2024-01-11"))
        dao.insertFoodEntry(makeEntry(dateString = "2024-01-12"))

        dao.getAllFoodEntries().test {
            assertEquals(3, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllFoodEntries_returnsEmptyWhenDatabaseIsEmpty() = runTest {
        dao.getAllFoodEntries().test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- getFoodEntriesForDateRange ---

    @Test
    fun getFoodEntriesForDateRange_returnsEntriesWithinRange() = runTest {
        dao.insertFoodEntry(makeEntry(name = "Before Range", dateString = "2024-01-05"))
        dao.insertFoodEntry(makeEntry(name = "Start of Range", dateString = "2024-01-10"))
        dao.insertFoodEntry(makeEntry(name = "Middle", dateString = "2024-01-12"))
        dao.insertFoodEntry(makeEntry(name = "End of Range", dateString = "2024-01-15"))
        dao.insertFoodEntry(makeEntry(name = "After Range", dateString = "2024-01-20"))

        dao.getFoodEntriesForDateRange("2024-01-10", "2024-01-15").test {
            val items = awaitItem()
            assertEquals(3, items.size)
            val names = items.map { it.name }
            assertTrue(names.contains("Start of Range"))
            assertTrue(names.contains("Middle"))
            assertTrue(names.contains("End of Range"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFoodEntriesForDateRange_returnsEmptyForRangeWithNoData() = runTest {
        dao.insertFoodEntry(makeEntry(dateString = "2024-01-15"))

        dao.getFoodEntriesForDateRange("2024-02-01", "2024-02-28").test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- getTotalCaloriesForDate ---

    @Test
    fun getTotalCaloriesForDate_returnsSumOfCalories() = runTest {
        dao.insertFoodEntry(makeEntry(calories = 300, dateString = "2024-01-15"))
        dao.insertFoodEntry(makeEntry(calories = 450, dateString = "2024-01-15"))
        dao.insertFoodEntry(makeEntry(calories = 250, dateString = "2024-01-15"))

        dao.getTotalCaloriesForDate("2024-01-15").test {
            assertEquals(1000, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getTotalCaloriesForDate_returnsNullWhenNoEntries() = runTest {
        dao.getTotalCaloriesForDate("2024-01-15").test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getTotalCaloriesForDate_onlySumsForTargetDate() = runTest {
        dao.insertFoodEntry(makeEntry(calories = 500, dateString = "2024-01-15"))
        dao.insertFoodEntry(makeEntry(calories = 999, dateString = "2024-01-16"))

        dao.getTotalCaloriesForDate("2024-01-15").test {
            assertEquals(500, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- Data persistence and fields ---

    @Test
    fun insertedEntry_hasAllFieldsPersisted() = runTest {
        val entry = FoodEntry(
            name = "Greek Yogurt",
            calories = 100,
            protein = 17f,
            carbs = 6f,
            fat = 0.7f,
            servingSize = 170f,
            servingUnit = "g",
            mealType = "BREAKFAST",
            dateString = "2024-03-01"
        )
        dao.insertFoodEntry(entry)

        dao.getFoodEntriesForDate("2024-03-01").test {
            val item = awaitItem().first()
            assertEquals("Greek Yogurt", item.name)
            assertEquals(100, item.calories)
            assertEquals(17f, item.protein)
            assertEquals(6f, item.carbs)
            assertEquals(0.7f, item.fat)
            assertEquals(170f, item.servingSize)
            assertEquals("g", item.servingUnit)
            assertEquals("BREAKFAST", item.mealType)
            assertEquals("2024-03-01", item.dateString)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertEntry_autoGeneratesId() = runTest {
        dao.insertFoodEntry(makeEntry(dateString = "2024-01-15"))
        dao.insertFoodEntry(makeEntry(dateString = "2024-01-15"))

        dao.getFoodEntriesForDate("2024-01-15").test {
            val items = awaitItem()
            val ids = items.map { it.id }.distinct()
            assertEquals(2, ids.size) // Two distinct IDs
            cancelAndIgnoreRemainingEvents()
        }
    }
}
