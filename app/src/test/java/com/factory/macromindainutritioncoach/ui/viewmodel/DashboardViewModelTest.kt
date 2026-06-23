package com.factory.macromindainutritioncoach.ui.viewmodel

import app.cash.turbine.test
import com.factory.macromindainutritioncoach.MacroMindApplication
import com.factory.macromindainutritioncoach.MainDispatcherRule
import com.factory.macromindainutritioncoach.data.local.entity.FoodEntry
import com.factory.macromindainutritioncoach.data.local.entity.UserProfile
import com.factory.macromindainutritioncoach.data.repository.NutritionRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mockApp: MacroMindApplication
    private lateinit var mockRepository: NutritionRepository
    private lateinit var viewModel: DashboardViewModel

    private val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    private fun makeEntry(
        name: String,
        calories: Int,
        protein: Float = 0f,
        carbs: Float = 0f,
        fat: Float = 0f,
        mealType: String = "LUNCH"
    ) = FoodEntry(
        name = name,
        calories = calories,
        protein = protein,
        carbs = carbs,
        fat = fat,
        servingSize = 100f,
        servingUnit = "g",
        mealType = mealType,
        dateString = today
    )

    @Before
    fun setup() {
        mockApp = mockk(relaxed = true)
        mockRepository = mockk(relaxed = true)
        every { mockApp.repository } returns mockRepository
        every { mockRepository.getFoodEntriesForDate(any()) } returns flowOf(emptyList())
        every { mockRepository.getUserProfile() } returns flowOf(null)

        viewModel = DashboardViewModel(mockApp)
    }

    // --- Initial state ---

    @Test
    fun `initial todayEntries is empty`() = runTest {
        viewModel.todayEntries.test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial dailyMacros are all zeros`() = runTest {
        viewModel.dailyMacros.test {
            val macros = awaitItem()
            assertEquals(0, macros.totalCalories)
            assertEquals(0f, macros.totalProtein)
            assertEquals(0f, macros.totalCarbs)
            assertEquals(0f, macros.totalFat)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial userProfile is null`() = runTest {
        viewModel.userProfile.test {
            assertEquals(null, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `today property returns current date in yyyy-MM-dd format`() {
        assertNotNull(viewModel.today)
        assertEquals(today, viewModel.today)
    }

    // --- dailyMacros calculation ---

    @Test
    fun `dailyMacros sums calories from all entries`() = runTest {
        val entries = listOf(
            makeEntry("Chicken", calories = 300, protein = 30f),
            makeEntry("Rice", calories = 200, carbs = 44f),
            makeEntry("Avocado", calories = 150, fat = 14f)
        )
        every { mockRepository.getFoodEntriesForDate(any()) } returns flowOf(entries)
        val vm = DashboardViewModel(mockApp)

        vm.dailyMacros.test {
            val macros = awaitItem()
            assertEquals(650, macros.totalCalories)
            assertEquals(30f, macros.totalProtein)
            assertEquals(44f, macros.totalCarbs)
            assertEquals(14f, macros.totalFat)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dailyMacros sums macros from multiple entries`() = runTest {
        val entries = listOf(
            makeEntry("Food A", 400, protein = 20f, carbs = 50f, fat = 10f),
            makeEntry("Food B", 300, protein = 15f, carbs = 30f, fat = 8f)
        )
        every { mockRepository.getFoodEntriesForDate(any()) } returns flowOf(entries)
        val vm = DashboardViewModel(mockApp)

        vm.dailyMacros.test {
            val macros = awaitItem()
            assertEquals(700, macros.totalCalories)
            assertEquals(35f, macros.totalProtein)
            assertEquals(80f, macros.totalCarbs)
            assertEquals(18f, macros.totalFat)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- mealBreakdown ---

    @Test
    fun `mealBreakdown groups entries by meal type`() = runTest {
        val entries = listOf(
            makeEntry("Eggs", 150, mealType = "BREAKFAST"),
            makeEntry("Sandwich", 400, mealType = "LUNCH"),
            makeEntry("Salad", 200, mealType = "LUNCH"),
            makeEntry("Steak", 500, mealType = "DINNER")
        )
        every { mockRepository.getFoodEntriesForDate(any()) } returns flowOf(entries)
        val vm = DashboardViewModel(mockApp)

        vm.mealBreakdown.test {
            val breakdown = awaitItem()
            assertEquals(1, breakdown["BREAKFAST"]?.size)
            assertEquals(2, breakdown["LUNCH"]?.size)
            assertEquals(1, breakdown["DINNER"]?.size)
            assertEquals(0, breakdown["SNACK"]?.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `mealBreakdown contains all four meal type keys`() = runTest {
        every { mockRepository.getFoodEntriesForDate(any()) } returns flowOf(emptyList())
        val vm = DashboardViewModel(mockApp)

        vm.mealBreakdown.test {
            val breakdown = awaitItem()
            assertTrue(breakdown.containsKey("BREAKFAST"))
            assertTrue(breakdown.containsKey("LUNCH"))
            assertTrue(breakdown.containsKey("DINNER"))
            assertTrue(breakdown.containsKey("SNACK"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- userProfile ---

    @Test
    fun `userProfile reflects repository value`() = runTest {
        val profile = UserProfile(id = 1, name = "Alice", targetCalories = 2000)
        every { mockRepository.getUserProfile() } returns flowOf(profile)
        val vm = DashboardViewModel(mockApp)

        vm.userProfile.test {
            val result = awaitItem()
            assertEquals("Alice", result?.name)
            assertEquals(2000, result?.targetCalories)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- deleteEntry ---

    @Test
    fun `deleteEntry calls repository deleteFoodEntry`() = runTest {
        val entry = makeEntry("Yogurt", 100)

        viewModel.deleteEntry(entry)

        // Give coroutine time to execute
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        io.mockk.coVerify { mockRepository.deleteFoodEntry(entry) }
    }
}
