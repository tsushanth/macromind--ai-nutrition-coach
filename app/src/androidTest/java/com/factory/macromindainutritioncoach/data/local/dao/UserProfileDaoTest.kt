package com.factory.macromindainutritioncoach.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.factory.macromindainutritioncoach.data.local.database.MacroMindDatabase
import com.factory.macromindainutritioncoach.data.local.entity.UserProfile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class UserProfileDaoTest {

    private lateinit var database: MacroMindDatabase
    private lateinit var dao: UserProfileDao

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
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MacroMindDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.userProfileDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    // --- insertOrUpdate & getUserProfile ---

    @Test
    fun insertProfile_canBeRetrievedViaFlow() = runTest {
        dao.insertOrUpdate(defaultProfile)

        dao.getUserProfile().test {
            val profile = awaitItem()
            assertEquals("Alice", profile?.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getUserProfile_returnsNullWhenEmpty() = runTest {
        dao.getUserProfile().test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertOrUpdate_updatesExistingProfileByPrimaryKey() = runTest {
        dao.insertOrUpdate(defaultProfile)
        val updated = defaultProfile.copy(name = "Alice Updated", weightKg = 63f)
        dao.insertOrUpdate(updated)

        dao.getUserProfile().test {
            val profile = awaitItem()
            assertEquals("Alice Updated", profile?.name)
            assertEquals(63f, profile?.weightKg)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertOrUpdate_persistsAllFields() = runTest {
        dao.insertOrUpdate(defaultProfile)

        dao.getUserProfile().test {
            val profile = awaitItem()
            assertEquals(1, profile?.id)
            assertEquals("Alice", profile?.name)
            assertEquals(28, profile?.age)
            assertEquals(65f, profile?.weightKg)
            assertEquals(168f, profile?.heightCm)
            assertEquals("FEMALE", profile?.gender)
            assertEquals("MODERATELY_ACTIVE", profile?.activityLevel)
            assertEquals("MAINTAIN", profile?.goal)
            assertEquals(2000, profile?.targetCalories)
            assertEquals(150, profile?.targetProtein)
            assertEquals(200, profile?.targetCarbs)
            assertEquals(66, profile?.targetFat)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- getUserProfileOnce ---

    @Test
    fun getUserProfileOnce_returnsNullWhenEmpty() = runTest {
        assertNull(dao.getUserProfileOnce())
    }

    @Test
    fun getUserProfileOnce_returnsProfileWhenExists() = runTest {
        dao.insertOrUpdate(defaultProfile)

        val profile = dao.getUserProfileOnce()
        assertEquals("Alice", profile?.name)
        assertEquals(1, profile?.id)
    }

    @Test
    fun getUserProfileOnce_returnsLatestAfterUpdate() = runTest {
        dao.insertOrUpdate(defaultProfile)
        dao.insertOrUpdate(defaultProfile.copy(name = "Alice v2", targetCalories = 1800))

        val profile = dao.getUserProfileOnce()
        assertEquals("Alice v2", profile?.name)
        assertEquals(1800, profile?.targetCalories)
    }

    // --- Edge cases ---

    @Test
    fun insertProfile_doesNotCreateDuplicatesForSameId() = runTest {
        dao.insertOrUpdate(defaultProfile)
        dao.insertOrUpdate(defaultProfile.copy(goal = "LOSE"))
        dao.insertOrUpdate(defaultProfile.copy(goal = "GAIN"))

        // Should still only have one profile row
        val profile = dao.getUserProfileOnce()
        assertEquals("GAIN", profile?.goal) // last one wins (REPLACE strategy)
    }

    @Test
    fun profileFlow_emitsNewValueAfterUpdate() = runTest {
        dao.getUserProfile().test {
            assertNull(awaitItem()) // initial null

            dao.insertOrUpdate(defaultProfile)
            val first = awaitItem()
            assertEquals("Alice", first?.name)

            dao.insertOrUpdate(defaultProfile.copy(name = "Alice Updated"))
            val second = awaitItem()
            assertEquals("Alice Updated", second?.name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- Goal types ---

    @Test
    fun insertProfile_withLoseGoal() = runTest {
        dao.insertOrUpdate(defaultProfile.copy(goal = "LOSE"))

        val profile = dao.getUserProfileOnce()
        assertEquals("LOSE", profile?.goal)
    }

    @Test
    fun insertProfile_withGainGoal() = runTest {
        dao.insertOrUpdate(defaultProfile.copy(goal = "GAIN"))

        val profile = dao.getUserProfileOnce()
        assertEquals("GAIN", profile?.goal)
    }

    // --- Gender types ---

    @Test
    fun insertProfile_withMaleGender() = runTest {
        dao.insertOrUpdate(defaultProfile.copy(gender = "MALE"))

        val profile = dao.getUserProfileOnce()
        assertEquals("MALE", profile?.gender)
    }

    // --- Activity levels ---

    @Test
    fun insertProfile_withSedentaryActivityLevel() = runTest {
        dao.insertOrUpdate(defaultProfile.copy(activityLevel = "SEDENTARY"))

        val profile = dao.getUserProfileOnce()
        assertEquals("SEDENTARY", profile?.activityLevel)
    }

    @Test
    fun insertProfile_withExtraActiveActivityLevel() = runTest {
        dao.insertOrUpdate(defaultProfile.copy(activityLevel = "EXTRA_ACTIVE"))

        val profile = dao.getUserProfileOnce()
        assertEquals("EXTRA_ACTIVE", profile?.activityLevel)
    }
}
