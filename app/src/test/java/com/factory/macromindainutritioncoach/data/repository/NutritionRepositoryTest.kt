package com.factory.macromindainutritioncoach.data.repository

import com.factory.macromindainutritioncoach.data.local.dao.CommonFoodDao
import com.factory.macromindainutritioncoach.data.local.dao.FoodEntryDao
import com.factory.macromindainutritioncoach.data.local.dao.UserProfileDao
import com.factory.macromindainutritioncoach.data.local.entity.CommonFood
import com.factory.macromindainutritioncoach.data.local.entity.FoodEntry
import com.factory.macromindainutritioncoach.data.local.entity.UserProfile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NutritionRepositoryTest {

    private lateinit var mockFoodEntryDao: FoodEntryDao
    private lateinit var mockUserProfileDao: UserProfileDao
    private lateinit var mockCommonFoodDao: CommonFoodDao
    private lateinit var repository: NutritionRepository

    private fun makeFoodEntry(
        name: String = "Chicken",
        calories: Int = 200,
        dateString: String = "2024-01-15"
    ) = FoodEntry(
        name = name,
        calories = calories,
        protein = 30f,
        carbs = 0f,
        fat = 5f,
        servingSize = 100f,
        servingUnit = "g",
        mealType = "LUNCH",
        dateString = dateString
    )

    @Before
    fun setup() {
        mockFoodEntryDao = mockk(relaxed = true)
        mockUserProfileDao = mockk(relaxed = true)
        mockCommonFoodDao = mockk(relaxed = true)
        repository = NutritionRepository(mockFoodEntryDao, mockUserProfileDao, mockCommonFoodDao)
    }

    // --- FoodEntry delegation ---

    @Test
    fun `insertFoodEntry delegates to dao`() = runTest {
        val entry = makeFoodEntry()
        coEvery { mockFoodEntryDao.insertFoodEntry(entry) } just Runs

        repository.insertFoodEntry(entry)

        coVerify { mockFoodEntryDao.insertFoodEntry(entry) }
    }

    @Test
    fun `deleteFoodEntry delegates to dao`() = runTest {
        val entry = makeFoodEntry()
        coEvery { mockFoodEntryDao.deleteFoodEntry(entry) } just Runs

        repository.deleteFoodEntry(entry)

        coVerify { mockFoodEntryDao.deleteFoodEntry(entry) }
    }

    @Test
    fun `getFoodEntriesForDate delegates to dao`() {
        val date = "2024-01-15"
        val expected = flowOf(listOf(makeFoodEntry(dateString = date)))
        every { mockFoodEntryDao.getFoodEntriesForDate(date) } returns expected

        val result = repository.getFoodEntriesForDate(date)

        verify { mockFoodEntryDao.getFoodEntriesForDate(date) }
        assertEquals(expected, result)
    }

    @Test
    fun `getAllFoodEntries delegates to dao`() {
        val expected = flowOf(listOf(makeFoodEntry()))
        every { mockFoodEntryDao.getAllFoodEntries() } returns expected

        val result = repository.getAllFoodEntries()

        verify { mockFoodEntryDao.getAllFoodEntries() }
        assertEquals(expected, result)
    }

    @Test
    fun `getFoodEntriesForDateRange delegates to dao`() {
        val start = "2024-01-01"
        val end = "2024-01-07"
        val expected = flowOf(listOf(makeFoodEntry()))
        every { mockFoodEntryDao.getFoodEntriesForDateRange(start, end) } returns expected

        val result = repository.getFoodEntriesForDateRange(start, end)

        verify { mockFoodEntryDao.getFoodEntriesForDateRange(start, end) }
        assertEquals(expected, result)
    }

    @Test
    fun `getTotalCaloriesForDate delegates to dao`() {
        val date = "2024-01-15"
        val expected = flowOf(500)
        every { mockFoodEntryDao.getTotalCaloriesForDate(date) } returns expected

        val result = repository.getTotalCaloriesForDate(date)

        verify { mockFoodEntryDao.getTotalCaloriesForDate(date) }
        assertEquals(expected, result)
    }

    // --- UserProfile delegation ---

    @Test
    fun `insertOrUpdateProfile delegates to dao`() = runTest {
        val profile = UserProfile(id = 1, name = "Alice")
        coEvery { mockUserProfileDao.insertOrUpdate(profile) } just Runs

        repository.insertOrUpdateProfile(profile)

        coVerify { mockUserProfileDao.insertOrUpdate(profile) }
    }

    @Test
    fun `getUserProfile delegates to dao`() {
        val expected = flowOf(UserProfile(id = 1, name = "Alice"))
        every { mockUserProfileDao.getUserProfile() } returns expected

        val result = repository.getUserProfile()

        verify { mockUserProfileDao.getUserProfile() }
        assertEquals(expected, result)
    }

    @Test
    fun `getUserProfileOnce delegates to dao`() = runTest {
        val expected = UserProfile(id = 1, name = "Alice")
        coEvery { mockUserProfileDao.getUserProfileOnce() } returns expected

        val result = repository.getUserProfileOnce()

        coVerify { mockUserProfileDao.getUserProfileOnce() }
        assertEquals(expected, result)
    }

    @Test
    fun `getUserProfileOnce returns null when no profile`() = runTest {
        coEvery { mockUserProfileDao.getUserProfileOnce() } returns null

        val result = repository.getUserProfileOnce()

        assertNull(result)
    }

    // --- CommonFood delegation ---

    @Test
    fun `searchFoods delegates to dao`() {
        val query = "chicken"
        val expected = flowOf(listOf<CommonFood>())
        every { mockCommonFoodDao.searchFoods(query) } returns expected

        val result = repository.searchFoods(query)

        verify { mockCommonFoodDao.searchFoods(query) }
        assertEquals(expected, result)
    }

    @Test
    fun `getAllFoods delegates to dao`() {
        val expected = flowOf(listOf<CommonFood>())
        every { mockCommonFoodDao.getAllFoods() } returns expected

        val result = repository.getAllFoods()

        verify { mockCommonFoodDao.getAllFoods() }
        assertEquals(expected, result)
    }

    @Test
    fun `getFoodsByCategory delegates to dao`() {
        val category = "PROTEIN"
        val expected = flowOf(listOf<CommonFood>())
        every { mockCommonFoodDao.getFoodsByCategory(category) } returns expected

        val result = repository.getFoodsByCategory(category)

        verify { mockCommonFoodDao.getFoodsByCategory(category) }
        assertEquals(expected, result)
    }

    // --- calculateTDEE ---

    @Test
    fun `calculateTDEE for male moderately active maintain`() {
        // BMR = 10*80 + 6.25*180 - 5*30 + 5 = 800 + 1125 - 150 + 5 = 1780
        // TDEE = 1780 * 1.55 = 2759
        val profile = UserProfile(
            gender = "MALE",
            weightKg = 80f,
            heightCm = 180f,
            age = 30,
            activityLevel = "MODERATELY_ACTIVE",
            goal = "MAINTAIN"
        )
        assertEquals(2759, repository.calculateTDEE(profile))
    }

    @Test
    fun `calculateTDEE for female sedentary lose applies deficit and floor`() {
        // BMR = 10*60 + 6.25*160 - 5*25 - 161 = 600 + 1000 - 125 - 161 = 1314
        // TDEE = 1314 * 1.2 = 1576
        // LOSE = 1576 - 500 = 1076 -> coerceAtLeast(1200) = 1200
        val profile = UserProfile(
            gender = "FEMALE",
            weightKg = 60f,
            heightCm = 160f,
            age = 25,
            activityLevel = "SEDENTARY",
            goal = "LOSE"
        )
        assertEquals(1200, repository.calculateTDEE(profile))
    }

    @Test
    fun `calculateTDEE for male very active gain adds surplus`() {
        // BMR = 10*90 + 6.25*185 - 5*28 + 5 = 900 + 1156.25 - 140 + 5 = 1921.25
        // TDEE = 1921.25 * 1.725 = 3314.16 -> 3314
        // GAIN = 3314 + 300 = 3614
        val profile = UserProfile(
            gender = "MALE",
            weightKg = 90f,
            heightCm = 185f,
            age = 28,
            activityLevel = "VERY_ACTIVE",
            goal = "GAIN"
        )
        val tdee = repository.calculateTDEE(profile)
        assertEquals(3614, tdee)
    }

    @Test
    fun `calculateTDEE with LIGHTLY_ACTIVE uses correct multiplier`() {
        val profile = UserProfile(
            gender = "MALE",
            weightKg = 70f,
            heightCm = 175f,
            age = 30,
            activityLevel = "LIGHTLY_ACTIVE",
            goal = "MAINTAIN"
        )
        // BMR = 10*70 + 6.25*175 - 5*30 + 5 = 700 + 1093.75 - 150 + 5 = 1648.75
        // TDEE = 1648.75 * 1.375 = 2267.03 -> 2267
        val tdee = repository.calculateTDEE(profile)
        assertEquals(2267, tdee)
    }

    @Test
    fun `calculateTDEE with EXTRA_ACTIVE uses 1_9 multiplier`() {
        val profile = UserProfile(
            gender = "MALE",
            weightKg = 75f,
            heightCm = 178f,
            age = 25,
            activityLevel = "EXTRA_ACTIVE",
            goal = "MAINTAIN"
        )
        // BMR = 10*75 + 6.25*178 - 5*25 + 5 = 750 + 1112.5 - 125 + 5 = 1742.5
        // TDEE = 1742.5 * 1.9 = 3310.75 -> 3310
        val tdee = repository.calculateTDEE(profile)
        assertEquals(3310, tdee)
    }

    // --- calculateMacros ---

    @Test
    fun `calculateMacros for MAINTAIN returns balanced split`() {
        // 2000 cal: protein=30%/4cal=150g, fat=30%/9cal=66g, carbs=40%/4cal=200g
        val (protein, carbs, fat) = repository.calculateMacros(2000, "MAINTAIN")
        assertEquals(150, protein)
        assertEquals(200, carbs)
        assertEquals(66, fat)
    }

    @Test
    fun `calculateMacros for LOSE returns high protein split`() {
        // 2000 cal: protein=35%/4=175g, fat=30%/9=66g, carbs=35%/4=175g
        val (protein, carbs, fat) = repository.calculateMacros(2000, "LOSE")
        assertEquals(175, protein)
        assertEquals(175, carbs)
        assertEquals(66, fat)
    }

    @Test
    fun `calculateMacros for GAIN returns high carb split`() {
        // 2000 cal: protein=30%/4=150g, fat=25%/9=55g, carbs=45%/4=225g
        val (protein, carbs, fat) = repository.calculateMacros(2000, "GAIN")
        assertEquals(150, protein)
        assertEquals(225, carbs)
        assertEquals(55, fat)
    }

    @Test
    fun `calculateMacros scales correctly for different calorie totals`() {
        val (protein, carbs, fat) = repository.calculateMacros(1500, "MAINTAIN")
        // protein=30%/4=112.5->112, fat=30%/9=50, carbs=40%/4=150
        assertEquals(112, protein)
        assertEquals(150, carbs)
        assertEquals(50, fat)
    }
}
