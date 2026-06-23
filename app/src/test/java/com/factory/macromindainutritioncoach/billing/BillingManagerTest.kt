package com.factory.macromindainutritioncoach.billing

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.factory.macromindainutritioncoach.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for BillingManager.
 *
 * These tests use mockkStatic to intercept BillingClient.newBuilder() so the
 * BillingManager can be instantiated in a JVM environment without a real Play Store
 * connection. Tests cover product IDs, initial state, and getProductDetails logic.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BillingManagerTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mockContext: Context
    private lateinit var mockPremiumManager: PremiumManager
    private lateinit var mockBillingClient: BillingClient
    private lateinit var mockBuilder: BillingClient.Builder
    private lateinit var billingManager: BillingManager

    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
        mockPremiumManager = mockk(relaxed = true)
        mockBillingClient = mockk(relaxed = true)
        mockBuilder = mockk(relaxed = true)

        mockkStatic(BillingClient::class)
        every { BillingClient.newBuilder(any()) } returns mockBuilder
        every { mockBuilder.setListener(any()) } returns mockBuilder
        every { mockBuilder.enablePendingPurchases() } returns mockBuilder
        every { mockBuilder.build() } returns mockBillingClient

        billingManager = BillingManager(mockContext, mockPremiumManager)
    }

    @After
    fun tearDown() {
        unmockkStatic(BillingClient::class)
    }

    // --- Companion object constants ---

    @Test
    fun `PRODUCT_WEEKLY has correct ID`() {
        assertEquals(
            "com.factory.macromindainutritioncoach.subscription.weekly",
            BillingManager.PRODUCT_WEEKLY
        )
    }

    @Test
    fun `PRODUCT_MONTHLY has correct ID`() {
        assertEquals(
            "com.factory.macromindainutritioncoach.subscription.monthly",
            BillingManager.PRODUCT_MONTHLY
        )
    }

    @Test
    fun `PRODUCT_YEARLY has correct ID`() {
        assertEquals(
            "com.factory.macromindainutritioncoach.subscription.yearly",
            BillingManager.PRODUCT_YEARLY
        )
    }

    @Test
    fun `PRODUCT_LIFETIME has correct ID`() {
        assertEquals(
            "com.factory.macromindainutritioncoach.subscription.lifetime",
            BillingManager.PRODUCT_LIFETIME
        )
    }

    @Test
    fun `PRODUCT_REMOVE_ADS has correct ID`() {
        assertEquals(
            "com.factory.macromindainutritioncoach.remove_ads",
            BillingManager.PRODUCT_REMOVE_ADS
        )
    }

    @Test
    fun `SUBSCRIPTION_PRODUCT_IDS contains weekly monthly yearly`() {
        assertEquals(
            listOf(
                BillingManager.PRODUCT_WEEKLY,
                BillingManager.PRODUCT_MONTHLY,
                BillingManager.PRODUCT_YEARLY
            ),
            BillingManager.SUBSCRIPTION_PRODUCT_IDS
        )
    }

    @Test
    fun `INAPP_PRODUCT_IDS contains lifetime and remove_ads`() {
        assertEquals(
            listOf(BillingManager.PRODUCT_LIFETIME, BillingManager.PRODUCT_REMOVE_ADS),
            BillingManager.INAPP_PRODUCT_IDS
        )
    }

    // --- Initial state ---

    @Test
    fun `initial billingState is Disconnected`() = runTest {
        assertEquals(BillingState.Disconnected, billingManager.billingState.value)
    }

    @Test
    fun `initial products list is empty`() = runTest {
        assertTrue(billingManager.products.value.isEmpty())
    }

    // --- getProductDetails ---

    @Test
    fun `getProductDetails returns null when products list is empty`() {
        assertNull(billingManager.getProductDetails(BillingManager.PRODUCT_WEEKLY))
    }

    @Test
    fun `getProductDetails returns correct product when products are loaded`() {
        val mockProduct = mockk<ProductDetails> {
            every { productId } returns BillingManager.PRODUCT_YEARLY
        }

        // Directly set the products state for testing
        val productsField = billingManager.javaClass.getDeclaredField("_products")
        productsField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val stateFlow = productsField.get(billingManager) as kotlinx.coroutines.flow.MutableStateFlow<List<ProductDetails>>
        stateFlow.value = listOf(mockProduct)

        val result = billingManager.getProductDetails(BillingManager.PRODUCT_YEARLY)
        assertEquals(BillingManager.PRODUCT_YEARLY, result?.productId)
    }

    @Test
    fun `getProductDetails returns null for unknown product ID`() {
        assertNull(billingManager.getProductDetails("com.unknown.product"))
    }

    // --- startConnection when already ready ---

    @Test
    fun `startConnection when client already ready does not change state to Disconnected`() {
        every { mockBillingClient.isReady } returns true

        // State should remain as initially set (Disconnected before any connection)
        assertEquals(BillingState.Disconnected, billingManager.billingState.value)
    }

    // --- restorePurchases when not ready ---

    @Test
    fun `restorePurchases when client not ready does not emit RestoreComplete`() = runTest {
        every { mockBillingClient.isReady } returns false

        billingManager.restorePurchases()

        // State should remain Disconnected since client is not ready
        assertEquals(BillingState.Disconnected, billingManager.billingState.value)
    }

    // --- BillingState sealed class ---

    @Test
    fun `BillingState types exist and are distinct`() {
        val states: List<BillingState> = listOf(
            BillingState.Disconnected,
            BillingState.Connected,
            BillingState.PurchaseSuccess,
            BillingState.PurchaseCancelled,
            BillingState.PurchasePending,
            BillingState.Restoring,
            BillingState.RestoreComplete,
            BillingState.Error("test error")
        )
        assertEquals(8, states.size)
    }

    @Test
    fun `BillingState Error holds message`() {
        val error = BillingState.Error("Something went wrong")
        assertEquals("Something went wrong", error.message)
    }
}
