package com.factory.macromindainutritioncoach.ui.viewmodel

import app.cash.turbine.test
import com.factory.macromindainutritioncoach.MacroMindApplication
import com.factory.macromindainutritioncoach.MainDispatcherRule
import com.factory.macromindainutritioncoach.data.local.entity.UserProfile
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
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mockApp: MacroMindApplication
    private lateinit var mockRepository: NutritionRepository
    private lateinit var viewModel: ProfileViewModel

    private val sampleProfile = UserProfile(
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
        every { mockRepository.getUserProfile() } returns flowOf(null)
        every { mockRepository.calculateTDEE(any()) } returns 2000
        every { mockRepository.calculateMacros(any(), any()) } returns Triple(150, 200, 66)

        viewModel = ProfileViewModel(mockApp)
    }

    // --- Initial state ---

    @Test
    fun `initial formState has default values`() = runTest {
        viewModel.formState.test {
            val state = awaitItem()
            assertEquals("", state.name)
            assertEquals("25", state.age)
            assertEquals("70", state.weightKg)
            assertEquals("170", state.heightCm)
            assertEquals("MALE", state.gender)
            assertEquals("MODERATELY_ACTIVE", state.activityLevel)
            assertEquals("MAINTAIN", state.goal)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isProfileComplete is false when profile is null`() = runTest {
        viewModel.isProfileComplete.test {
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isProfileComplete is true when profile has a name`() = runTest {
        every { mockRepository.getUserProfile() } returns flowOf(sampleProfile)
        val vm = ProfileViewModel(mockApp)

        vm.isProfileComplete.test {
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isProfileComplete is false when profile name is blank`() = runTest {
        every { mockRepository.getUserProfile() } returns flowOf(sampleProfile.copy(name = ""))
        val vm = ProfileViewModel(mockApp)

        vm.isProfileComplete.test {
            assertFalse(awaitItem())
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

    // --- Field updates ---

    @Test
    fun `updateName changes formState name`() = runTest {
        viewModel.updateName("Bob")
        viewModel.formState.test {
            assertEquals("Bob", awaitItem().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateAge changes formState age`() = runTest {
        viewModel.updateAge("30")
        viewModel.formState.test {
            assertEquals("30", awaitItem().age)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateWeightKg changes formState weightKg`() = runTest {
        viewModel.updateWeightKg("75")
        viewModel.formState.test {
            assertEquals("75", awaitItem().weightKg)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateHeightCm changes formState heightCm`() = runTest {
        viewModel.updateHeightCm("180")
        viewModel.formState.test {
            assertEquals("180", awaitItem().heightCm)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateGender changes formState gender`() = runTest {
        viewModel.updateGender("FEMALE")
        viewModel.formState.test {
            assertEquals("FEMALE", awaitItem().gender)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateActivityLevel changes formState activityLevel`() = runTest {
        viewModel.updateActivityLevel("VERY_ACTIVE")
        viewModel.formState.test {
            assertEquals("VERY_ACTIVE", awaitItem().activityLevel)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateGoal changes formState goal`() = runTest {
        viewModel.updateGoal("LOSE")
        viewModel.formState.test {
            assertEquals("LOSE", awaitItem().goal)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- loadFromProfile ---

    @Test
    fun `loadFromProfile populates formState from profile`() = runTest {
        viewModel.loadFromProfile(sampleProfile)

        viewModel.formState.test {
            val state = awaitItem()
            assertEquals("Alice", state.name)
            assertEquals("28", state.age)
            assertEquals("65.0", state.weightKg)
            assertEquals("168.0", state.heightCm)
            assertEquals("FEMALE", state.gender)
            assertEquals("MODERATELY_ACTIVE", state.activityLevel)
            assertEquals("MAINTAIN", state.goal)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- saveProfile ---

    @Test
    fun `saveProfile calls repository insertOrUpdateProfile`() = runTest {
        viewModel.updateName("Charlie")
        viewModel.updateAge("32")
        viewModel.updateWeightKg("80")
        viewModel.updateHeightCm("175")

        viewModel.saveProfile()

        coVerify { mockRepository.insertOrUpdateProfile(any()) }
    }

    @Test
    fun `saveProfile sets saveSuccess to true`() = runTest {
        viewModel.updateName("Charlie")
        viewModel.saveProfile()

        viewModel.saveSuccess.test {
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveProfile creates profile with id 1`() = runTest {
        viewModel.updateName("Test User")
        viewModel.saveProfile()

        coVerify {
            mockRepository.insertOrUpdateProfile(withArg { profile ->
                assertEquals(1, profile.id)
                assertEquals("Test User", profile.name)
            })
        }
    }

    // --- resetSaveSuccess ---

    @Test
    fun `resetSaveSuccess sets saveSuccess back to false`() = runTest {
        viewModel.updateName("Test")
        viewModel.saveProfile()
        viewModel.resetSaveSuccess()

        viewModel.saveSuccess.test {
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
