package com.factory.macromindainutritioncoach.ui.viewmodel

import app.cash.turbine.test
import com.factory.macromindainutritioncoach.MacroMindApplication
import com.factory.macromindainutritioncoach.MainDispatcherRule
import com.factory.macromindainutritioncoach.data.local.entity.CommonFood
import com.factory.macromindainutritioncoach.data.local.entity.FoodEntry
import com.factory.macromindainutritioncoach.data.repository.NutritionRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FoodLogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mockApp: MacroMindApplication
    private lateinit var mockRepository: NutritionRepository
    private lateinit var viewModel: FoodLogViewModel

    private val sampleCommonFood = CommonFood(
        id = 1L,
        name = "Chicken Breast",
        caloriesPer100g = 165,
        proteinPer100g = 31f,
        carbsPer100g = 0f,
        fatPer100g = 3.6f,
        defaultServingSize = 100f,
        defaultServingUnit = "g",
        category = "PROTEIN"
    )

    @Before
    fun setup() {
        mockApp = mockk(relaxed = true)
        mockRepository = mockk(relaxed = true)
        every { mockApp.repository } returns mockRepository
        every { mockRepository.getAllFoods() } returns flowOf(listOf(sampleCommonFood))
        every { mockRepository.searchFoods(any()) } returns flowOf(emptyList())

        viewModel = FoodLogViewModel(mockApp)
    }

    // --- Initial state ---

    @Test
    fun `initial searchQuery is empty`() = runTest {
        viewModel.searchQuery.test {
            assertEquals("", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial selectedMealType is BREAKFAST`() = runTest {
        viewModel.selectedMealType.test {
            assertEquals("BREAKFAST", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial formState has empty fields`() = runTest {
        viewModel.formState.test {
            val state = awaitItem()
            assertEquals("", state.name)
            assertEquals("", state.calories)
            assertEquals("BREAKFAST", state.mealType)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial saveSuccess is false`() = runTest {
        viewModel.saveSuccess.test {
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- updateSearchQuery ---

    @Test
    fun `updateSearchQuery updates searchQuery state`() = runTest {
        viewModel.updateSearchQuery("rice")

        viewModel.searchQuery.test {
            assertEquals("rice", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- updateMealType ---

    @Test
    fun `updateMealType updates selectedMealType`() = runTest {
        viewModel.updateMealType("DINNER")

        viewModel.selectedMealType.test {
            assertEquals("DINNER", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateMealType also updates formState mealType`() = runTest {
        viewModel.updateMealType("SNACK")

        viewModel.formState.test {
            assertEquals("SNACK", awaitItem().mealType)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- updateFormField ---

    @Test
    fun `updateFormField name updates formState`() = runTest {
        viewModel.updateFormField("name", "Apple")

        viewModel.formState.test {
            assertEquals("Apple", awaitItem().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateFormField calories updates formState`() = runTest {
        viewModel.updateFormField("calories", "250")

        viewModel.formState.test {
            assertEquals("250", awaitItem().calories)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateFormField protein updates formState`() = runTest {
        viewModel.updateFormField("protein", "15.5")

        viewModel.formState.test {
            assertEquals("15.5", awaitItem().protein)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateFormField carbs updates formState`() = runTest {
        viewModel.updateFormField("carbs", "30.0")

        viewModel.formState.test {
            assertEquals("30.0", awaitItem().carbs)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateFormField fat updates formState`() = runTest {
        viewModel.updateFormField("fat", "5.0")

        viewModel.formState.test {
            assertEquals("5.0", awaitItem().fat)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateFormField unknown field leaves formState unchanged`() = runTest {
        val before = viewModel.formState.value
        viewModel.updateFormField("unknown_field", "value")

        viewModel.formState.test {
            assertEquals(before, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- selectCommonFood ---

    @Test
    fun `selectCommonFood fills formState with scaled nutrition`() = runTest {
        viewModel.selectCommonFood(sampleCommonFood, 150f)

        viewModel.formState.test {
            val state = awaitItem()
            assertEquals("Chicken Breast", state.name)
            // 165 * 1.5 = 247.5 -> 247
            assertEquals("247", state.calories)
            assertEquals("150.0", state.servingSize)
            assertEquals("g", state.servingUnit)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- saveFoodEntry ---

    @Test
    fun `saveFoodEntry returns false when name is blank`() {
        viewModel.updateFormField("name", "")
        viewModel.updateFormField("calories", "200")

        val result = viewModel.saveFoodEntry()

        assertFalse(result)
    }

    @Test
    fun `saveFoodEntry returns false when calories is not a number`() {
        viewModel.updateFormField("name", "Apple")
        viewModel.updateFormField("calories", "abc")

        val result = viewModel.saveFoodEntry()

        assertFalse(result)
    }

    @Test
    fun `saveFoodEntry returns true with valid form data`() = runTest {
        viewModel.updateFormField("name", "Apple")
        viewModel.updateFormField("calories", "95")
        viewModel.updateFormField("protein", "0.5")
        viewModel.updateFormField("carbs", "25.0")
        viewModel.updateFormField("fat", "0.3")

        val result = viewModel.saveFoodEntry()

        assertTrue(result)
    }

    @Test
    fun `saveFoodEntry with valid data calls repository insertFoodEntry`() = runTest {
        viewModel.updateFormField("name", "Banana")
        viewModel.updateFormField("calories", "105")

        viewModel.saveFoodEntry()

        coVerify { mockRepository.insertFoodEntry(any()) }
    }

    @Test
    fun `saveFoodEntry sets saveSuccess to true`() = runTest {
        viewModel.updateFormField("name", "Banana")
        viewModel.updateFormField("calories", "105")

        viewModel.saveFoodEntry()

        viewModel.saveSuccess.test {
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- resetSaveSuccess ---

    @Test
    fun `resetSaveSuccess sets saveSuccess back to false`() = runTest {
        viewModel.updateFormField("name", "Banana")
        viewModel.updateFormField("calories", "105")
        viewModel.saveFoodEntry()

        viewModel.resetSaveSuccess()

        viewModel.saveSuccess.test {
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
