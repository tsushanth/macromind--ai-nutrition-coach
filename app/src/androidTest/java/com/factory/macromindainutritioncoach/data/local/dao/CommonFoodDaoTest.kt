package com.factory.macromindainutritioncoach.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.factory.macromindainutritioncoach.data.local.database.MacroMindDatabase
import com.factory.macromindainutritioncoach.data.local.entity.CommonFood
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class CommonFoodDaoTest {

    private lateinit var database: MacroMindDatabase
    private lateinit var dao: CommonFoodDao

    private fun makeFood(
        name: String,
        calories: Int = 100,
        protein: Float = 5f,
        carbs: Float = 15f,
        fat: Float = 3f,
        category: String = "GENERAL"
    ) = CommonFood(
        name = name,
        caloriesPer100g = calories,
        proteinPer100g = protein,
        carbsPer100g = carbs,
        fatPer100g = fat,
        defaultServingSize = 100f,
        defaultServingUnit = "g",
        category = category
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MacroMindDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.commonFoodDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    // --- insertAll & getAllFoods ---

    @Test
    fun insertAll_canBeRetrievedViaGetAllFoods() = runTest {
        dao.insertAll(
            makeFood("Apple", calories = 52, category = "FRUIT"),
            makeFood("Banana", calories = 89, category = "FRUIT")
        )

        dao.getAllFoods().test {
            val items = awaitItem()
            assertEquals(2, items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllFoods_returnsEmptyWhenDatabaseIsEmpty() = runTest {
        dao.getAllFoods().test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllFoods_returnedInAlphabeticalOrder() = runTest {
        dao.insertAll(
            makeFood("Zucchini"),
            makeFood("Apple"),
            makeFood("Mango")
        )

        dao.getAllFoods().test {
            val names = awaitItem().map { it.name }
            assertEquals(listOf("Apple", "Mango", "Zucchini"), names)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- searchFoods ---

    @Test
    fun searchFoods_returnsMatchingByName() = runTest {
        dao.insertAll(
            makeFood("Chicken Breast", category = "PROTEIN"),
            makeFood("Chicken Thigh", category = "PROTEIN"),
            makeFood("Beef Steak", category = "PROTEIN")
        )

        dao.searchFoods("chicken").test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertTrue(items.all { it.name.contains("Chicken", ignoreCase = true) })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchFoods_isCaseInsensitive() = runTest {
        dao.insertAll(makeFood("Greek Yogurt", category = "DAIRY"))

        dao.searchFoods("GREEK").test {
            val items = awaitItem()
            assertEquals(1, items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchFoods_returnsEmptyWhenNoMatch() = runTest {
        dao.insertAll(makeFood("Apple"), makeFood("Banana"))

        dao.searchFoods("xyz_no_match").test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchFoods_returnsAllWhenQueryMatchesAll() = runTest {
        dao.insertAll(
            makeFood("Brown Rice"),
            makeFood("White Rice"),
            makeFood("Rice Cakes")
        )

        dao.searchFoods("Rice").test {
            val items = awaitItem()
            assertEquals(3, items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchFoods_returnsEmptyListForEmptyDatabase() = runTest {
        dao.searchFoods("chicken").test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- getFoodsByCategory ---

    @Test
    fun getFoodsByCategory_returnsOnlyMatchingCategory() = runTest {
        dao.insertAll(
            makeFood("Chicken", category = "PROTEIN"),
            makeFood("Beef", category = "PROTEIN"),
            makeFood("Apple", category = "FRUIT"),
            makeFood("Broccoli", category = "VEGETABLE")
        )

        dao.getFoodsByCategory("PROTEIN").test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertTrue(items.all { it.category == "PROTEIN" })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFoodsByCategory_returnsEmptyForUnknownCategory() = runTest {
        dao.insertAll(makeFood("Apple", category = "FRUIT"))

        dao.getFoodsByCategory("UNKNOWN").test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getFoodsByCategory_returnsAlphabeticalOrder() = runTest {
        dao.insertAll(
            makeFood("Salmon", category = "PROTEIN"),
            makeFood("Chicken", category = "PROTEIN"),
            makeFood("Tuna", category = "PROTEIN")
        )

        dao.getFoodsByCategory("PROTEIN").test {
            val names = awaitItem().map { it.name }
            assertEquals(listOf("Chicken", "Salmon", "Tuna"), names)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- getCount ---

    @Test
    fun getCount_returnsZeroWhenEmpty() = runTest {
        assertEquals(0, dao.getCount())
    }

    @Test
    fun getCount_returnsCorrectCountAfterInserts() = runTest {
        dao.insertAll(
            makeFood("Food 1"),
            makeFood("Food 2"),
            makeFood("Food 3")
        )
        assertEquals(3, dao.getCount())
    }

    // --- Data persistence ---

    @Test
    fun insertedFood_hasAllFieldsPersisted() = runTest {
        val food = CommonFood(
            name = "Salmon Fillet",
            caloriesPer100g = 208,
            proteinPer100g = 20f,
            carbsPer100g = 0f,
            fatPer100g = 13f,
            defaultServingSize = 150f,
            defaultServingUnit = "g",
            category = "PROTEIN"
        )
        dao.insertAll(food)

        dao.getAllFoods().test {
            val item = awaitItem().first()
            assertEquals("Salmon Fillet", item.name)
            assertEquals(208, item.caloriesPer100g)
            assertEquals(20f, item.proteinPer100g)
            assertEquals(0f, item.carbsPer100g)
            assertEquals(13f, item.fatPer100g)
            assertEquals(150f, item.defaultServingSize)
            assertEquals("g", item.defaultServingUnit)
            assertEquals("PROTEIN", item.category)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertAll_withReplaceConflict_updatesExistingRow() = runTest {
        val food = CommonFood(
            id = 1L,
            name = "Original Name",
            caloriesPer100g = 100,
            proteinPer100g = 5f,
            carbsPer100g = 10f,
            fatPer100g = 2f,
            defaultServingSize = 100f,
            defaultServingUnit = "g",
            category = "GENERAL"
        )
        dao.insertAll(food)

        val updated = food.copy(name = "Updated Name", caloriesPer100g = 120)
        dao.insertAll(updated)

        dao.getAllFoods().test {
            val items = awaitItem()
            assertEquals(1, items.size) // should still be just one row
            assertEquals("Updated Name", items[0].name)
            assertEquals(120, items[0].caloriesPer100g)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- Auto-generated IDs ---

    @Test
    fun insertedFoods_haveDistinctAutoGeneratedIds() = runTest {
        dao.insertAll(makeFood("Food A"), makeFood("Food B"), makeFood("Food C"))

        dao.getAllFoods().test {
            val ids = awaitItem().map { it.id }
            assertEquals(3, ids.distinct().size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
