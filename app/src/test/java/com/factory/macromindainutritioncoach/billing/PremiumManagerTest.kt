package com.factory.macromindainutritioncoach.billing

import android.content.Context
import android.content.SharedPreferences
import app.cash.turbine.test
import com.factory.macromindainutritioncoach.MainDispatcherRule
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PremiumManagerTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mockContext: Context
    private lateinit var mockPrefs: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor
    private lateinit var premiumManager: PremiumManager

    @Before
    fun setup() {
        mockContext = mockk()
        mockPrefs = mockk()
        mockEditor = mockk {
            every { putBoolean(any(), any()) } returns this
            every { putString(any(), any()) } returns this
            every { apply() } just Runs
        }

        every { mockContext.getSharedPreferences("macromind_premium", Context.MODE_PRIVATE) } returns mockPrefs
        every { mockPrefs.getBoolean("is_premium", false) } returns false
        every { mockPrefs.getBoolean("ads_removed", false) } returns false
        every { mockPrefs.getString("active_product", null) } returns null
        every { mockPrefs.edit() } returns mockEditor

        premiumManager = PremiumManager(mockContext)
    }

    // --- Initial state ---

    @Test
    fun `initial isPremium is false`() = runTest {
        premiumManager.isPremium.test {
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial adsRemoved is false`() = runTest {
        premiumManager.adsRemoved.test {
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial activeProduct is null`() = runTest {
        premiumManager.activeProduct.test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial activeProductLabel is null`() {
        assertNull(premiumManager.activeProductLabel())
    }

    @Test
    fun `isPremium reflects persisted true value on creation`() = runTest {
        every { mockPrefs.getBoolean("is_premium", false) } returns true
        every { mockPrefs.getString("active_product", null) } returns BillingManager.PRODUCT_MONTHLY

        val manager = PremiumManager(mockContext)

        manager.isPremium.test {
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- grantPremiumAccess ---

    @Test
    fun `grantPremiumAccess with weekly sets isPremium and activeProduct`() = runTest {
        premiumManager.grantPremiumAccess(listOf(BillingManager.PRODUCT_WEEKLY))

        assertTrue(premiumManager.isPremium.value)
        assertEquals(BillingManager.PRODUCT_WEEKLY, premiumManager.activeProduct.value)
    }

    @Test
    fun `grantPremiumAccess with monthly sets isPremium and activeProduct`() {
        premiumManager.grantPremiumAccess(listOf(BillingManager.PRODUCT_MONTHLY))

        assertTrue(premiumManager.isPremium.value)
        assertEquals(BillingManager.PRODUCT_MONTHLY, premiumManager.activeProduct.value)
    }

    @Test
    fun `grantPremiumAccess with yearly sets isPremium and activeProduct`() {
        premiumManager.grantPremiumAccess(listOf(BillingManager.PRODUCT_YEARLY))

        assertTrue(premiumManager.isPremium.value)
        assertEquals(BillingManager.PRODUCT_YEARLY, premiumManager.activeProduct.value)
    }

    @Test
    fun `grantPremiumAccess with lifetime sets isPremium and activeProduct`() {
        premiumManager.grantPremiumAccess(listOf(BillingManager.PRODUCT_LIFETIME))

        assertTrue(premiumManager.isPremium.value)
        assertEquals(BillingManager.PRODUCT_LIFETIME, premiumManager.activeProduct.value)
    }

    @Test
    fun `grantPremiumAccess with remove_ads sets adsRemoved but not isPremium`() {
        premiumManager.grantPremiumAccess(listOf(BillingManager.PRODUCT_REMOVE_ADS))

        assertTrue(premiumManager.adsRemoved.value)
        assertFalse(premiumManager.isPremium.value)
        assertNull(premiumManager.activeProduct.value)
    }

    @Test
    fun `grantPremiumAccess with premium and ads sets both flags`() {
        premiumManager.grantPremiumAccess(
            listOf(BillingManager.PRODUCT_YEARLY, BillingManager.PRODUCT_REMOVE_ADS)
        )

        assertTrue(premiumManager.isPremium.value)
        assertTrue(premiumManager.adsRemoved.value)
        assertEquals(BillingManager.PRODUCT_YEARLY, premiumManager.activeProduct.value)
    }

    @Test
    fun `grantPremiumAccess emits updated isPremium via StateFlow`() = runTest {
        premiumManager.isPremium.test {
            assertFalse(awaitItem()) // initial
            premiumManager.grantPremiumAccess(listOf(BillingManager.PRODUCT_YEARLY))
            assertTrue(awaitItem()) // after grant
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- revokePremiumAccess ---

    @Test
    fun `revokePremiumAccess sets isPremium false and clears activeProduct`() {
        premiumManager.grantPremiumAccess(listOf(BillingManager.PRODUCT_MONTHLY))
        premiumManager.revokePremiumAccess()

        assertFalse(premiumManager.isPremium.value)
        assertNull(premiumManager.activeProduct.value)
    }

    @Test
    fun `revokePremiumAccess emits via StateFlow`() = runTest {
        premiumManager.isPremium.test {
            assertFalse(awaitItem())
            premiumManager.grantPremiumAccess(listOf(BillingManager.PRODUCT_YEARLY))
            assertTrue(awaitItem())
            premiumManager.revokePremiumAccess()
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- activeProductLabel ---

    @Test
    fun `activeProductLabel returns Weekly for weekly product`() {
        premiumManager.grantPremiumAccess(listOf(BillingManager.PRODUCT_WEEKLY))
        assertEquals("Weekly", premiumManager.activeProductLabel())
    }

    @Test
    fun `activeProductLabel returns Monthly for monthly product`() {
        premiumManager.grantPremiumAccess(listOf(BillingManager.PRODUCT_MONTHLY))
        assertEquals("Monthly", premiumManager.activeProductLabel())
    }

    @Test
    fun `activeProductLabel returns Yearly for yearly product`() {
        premiumManager.grantPremiumAccess(listOf(BillingManager.PRODUCT_YEARLY))
        assertEquals("Yearly", premiumManager.activeProductLabel())
    }

    @Test
    fun `activeProductLabel returns Lifetime for lifetime product`() {
        premiumManager.grantPremiumAccess(listOf(BillingManager.PRODUCT_LIFETIME))
        assertEquals("Lifetime", premiumManager.activeProductLabel())
    }

    // --- SharedPreferences persistence ---

    @Test
    fun `grantPremiumAccess writes to SharedPreferences`() {
        premiumManager.grantPremiumAccess(listOf(BillingManager.PRODUCT_MONTHLY))

        verify { mockEditor.putBoolean("is_premium", true) }
        verify { mockEditor.putString("active_product", BillingManager.PRODUCT_MONTHLY) }
        verify { mockEditor.apply() }
    }

    @Test
    fun `grantPremiumAccess with remove_ads writes ads_removed to prefs`() {
        premiumManager.grantPremiumAccess(listOf(BillingManager.PRODUCT_REMOVE_ADS))

        verify { mockEditor.putBoolean("ads_removed", true) }
        verify { mockEditor.apply() }
    }

    @Test
    fun `revokePremiumAccess writes false to SharedPreferences`() {
        premiumManager.grantPremiumAccess(listOf(BillingManager.PRODUCT_YEARLY))
        premiumManager.revokePremiumAccess()

        verify { mockEditor.putBoolean("is_premium", false) }
        verify { mockEditor.putString("active_product", null) }
    }
}
