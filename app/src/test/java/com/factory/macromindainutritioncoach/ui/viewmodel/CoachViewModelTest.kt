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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class CoachViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mockApp: MacroMindApplication
    private lateinit var mockRepository: NutritionRepository
    private lateinit var viewModel: CoachViewModel

    private val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    private fun makeEntry(
        name: String = "Food",
        calories: Int = 300,
        protein: Float = 20f,
        mealType: String = "LUNCH",
        dateString: String = today
    ) = FoodEntry(
        name = name,
        calories = calories,
        protein = protein,
        carbs = 30f,
        fat = 10f,
        servingSize = 100f,
        servingUnit = "g",
        mealType = mealType,
        dateString = dateString
    )

    private val defaultProfile = UserProfile(
        id = 1,
        name = "Alice",
        age = 28,
        weightKg = 65f,
        heightCm = 168f,
        gender = "FEMALE",
        activityLevel = "MODERATELY_ACTIVE",
        goal = "MAINTAIN",
        targetCalories = 2000,
        targetProtein = 150,
        targetCarbs = 200,
        targetFat = 66
    )

    @Before
    fun setup() {
        mockApp = mockk(relaxed = true)
        mockRepository = mockk(relaxed = true)
        every { mockApp.repository } returns mockRepository
        every { mockRepository.getFoodEntriesForDateRange(any(), any()) } returns flowOf(emptyList())
        every { mockRepository.getUserProfile() } returns flowOf(null)

        viewModel = CoachViewModel(mockApp)
    }

    // --- Initial state ---

    @Test
    fun `initial weeklyInsights has zero values`() = runTest {
        viewModel.weeklyInsights.test {
            val insights = awaitItem()
            assertEquals(0, insights.avgCalories)
            assertEquals(0f, insights.avgProtein)
            assertEquals(0, insights.streak)
            assertEquals("", insights.bestDay)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial coachingTips are default tips when no profile`() = runTest {
        viewModel.coachingTips.test {
            val tips = awaitItem()
            assertTrue(tips.isNotEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- coachingTips with no profile ---

    @Test
    fun `coachingTips includes profile setup tip when profile is null`() = runTest {
        viewModel.coachingTips.test {
            val tips = awaitItem()
            assertTrue(tips.any { it.title == "Set Up Your Profile" })
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- coachingTips with profile and entries ---

    @Test
    fun `coachingTips includes start logging tip when no entries today`() = runTest {
        every { mockRepository.getUserProfile() } returns flowOf(defaultProfile)
        every { mockRepository.getFoodEntriesForDateRange(any(), any()) } returns flowOf(emptyList())
        val vm = CoachViewModel(mockApp)

        vm.coachingTips.test {
            val tips = awaitItem()
            assertTrue(tips.any { it.title == "Start Logging Today!" })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `coachingTips includes under calorie tip when far below target`() = runTest {
        val entries = listOf(makeEntry(calories = 500, protein = 20f)) // well under 2000 * 0.7
        every { mockRepository.getUserProfile() } returns flowOf(defaultProfile)
        every { mockRepository.getFoodEntriesForDateRange(any(), any()) } returns flowOf(entries)
        val vm = CoachViewModel(mockApp)

        vm.coachingTips.test {
            val tips = awaitItem()
            assertTrue(tips.any { it.title == "You're Under Your Calorie Goal" })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `coachingTips includes protein boost tip when protein is low`() = runTest {
        val entries = listOf(makeEntry(calories = 1800, protein = 30f)) // < 150 * 0.6 = 90
        every { mockRepository.getUserProfile() } returns flowOf(defaultProfile)
        every { mockRepository.getFoodEntriesForDateRange(any(), any()) } returns flowOf(entries)
        val vm = CoachViewModel(mockApp)

        vm.coachingTips.test {
            val tips = awaitItem()
            assertTrue(tips.any { it.title == "Boost Your Protein Intake" })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `coachingTips includes LOSE goal tip when profile goal is LOSE`() = runTest {
        every { mockRepository.getUserProfile() } returns flowOf(defaultProfile.copy(goal = "LOSE"))
        every { mockRepository.getFoodEntriesForDateRange(any(), any()) } returns flowOf(
            listOf(makeEntry(calories = 1600, protein = 120f))
        )
        val vm = CoachViewModel(mockApp)

        vm.coachingTips.test {
            val tips = awaitItem()
            assertTrue(tips.any { it.title == "Weight Loss Tip" })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `coachingTips includes GAIN goal tip when profile goal is GAIN`() = runTest {
        every { mockRepository.getUserProfile() } returns flowOf(defaultProfile.copy(goal = "GAIN"))
        every { mockRepository.getFoodEntriesForDateRange(any(), any()) } returns flowOf(
            listOf(makeEntry(calories = 1800, protein = 120f))
        )
        val vm = CoachViewModel(mockApp)

        vm.coachingTips.test {
            val tips = awaitItem()
            assertTrue(tips.any { it.title == "Muscle Building Tip" })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `coachingTips returns max 6 tips`() = runTest {
        val entries = listOf(makeEntry(calories = 1800, protein = 30f))
        every { mockRepository.getUserProfile() } returns flowOf(defaultProfile)
        every { mockRepository.getFoodEntriesForDateRange(any(), any()) } returns flowOf(entries)
        val vm = CoachViewModel(mockApp)

        vm.coachingTips.test {
            val tips = awaitItem()
            assertTrue(tips.size <= 6)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `coachingTips includes hydration tip`() = runTest {
        val entries = listOf(makeEntry(calories = 1900, protein = 140f))
        every { mockRepository.getUserProfile() } returns flowOf(defaultProfile)
        every { mockRepository.getFoodEntriesForDateRange(any(), any()) } returns flowOf(entries)
        val vm = CoachViewModel(mockApp)

        vm.coachingTips.test {
            val tips = awaitItem()
            assertTrue(tips.any { it.title == "Stay Hydrated" })
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- weeklyInsights ---

    @Test
    fun `weeklyInsights calculates avgCalories from entries`() = runTest {
        val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val entries = listOf(
            makeEntry(calories = 1800, dateString = today),
            makeEntry(calories = 2200, dateString = yesterday)
        )
        every { mockRepository.getFoodEntriesForDateRange(any(), any()) } returns flowOf(entries)
        every { mockRepository.getUserProfile() } returns flowOf(defaultProfile)
        val vm = CoachViewModel(mockApp)

        vm.weeklyInsights.test {
            val insights = awaitItem()
            assertEquals(2000, insights.avgCalories) // avg of 1800 and 2200
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `weeklyInsights streak is at least 1 when there are entries today`() = runTest {
        val entries = listOf(makeEntry(calories = 2000, dateString = today))
        every { mockRepository.getFoodEntriesForDateRange(any(), any()) } returns flowOf(entries)
        every { mockRepository.getUserProfile() } returns flowOf(defaultProfile)
        val vm = CoachViewModel(mockApp)

        vm.weeklyInsights.test {
            val insights = awaitItem()
            assertTrue(insights.streak >= 1)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
