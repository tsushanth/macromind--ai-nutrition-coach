package com.factory.macromindainutritioncoach.ui.viewmodel

import app.cash.turbine.test
import com.factory.macromindainutritioncoach.MacroMindApplication
import com.factory.macromindainutritioncoach.MainDispatcherRule
import com.factory.macromindainutritioncoach.billing.BillingManager
import com.factory.macromindainutritioncoach.billing.BillingState
import com.factory.macromindainutritioncoach.billing.PremiumManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PaywallViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mockApp: MacroMindApplication
    private lateinit var mockBillingManager: BillingManager
    private lateinit var mockPremiumManager: PremiumManager
    private lateinit var viewModel: PaywallViewModel

    private val billingStateFlow = MutableStateFlow<BillingState>(BillingState.Disconnected)
    private val isPremiumFlow = MutableStateFlow(false)
    private val activeProductFlow = MutableStateFlow<String?>(null)
    private val productsFlow = MutableStateFlow(emptyList<com.android.billingclient.api.ProductDetails>())

    @Before
    fun setup() {
        mockApp = mockk(relaxed = true)
        mockBillingManager = mockk(relaxed = true)
        mockPremiumManager = mockk(relaxed = true)

        every { mockApp.billingManager } returns mockBillingManager
        every { mockApp.premiumManager } returns mockPremiumManager

        every { mockBillingManager.billingState } returns billingStateFlow
        every { mockBillingManager.products } returns productsFlow
        every { mockPremiumManager.isPremium } returns isPremiumFlow
        every { mockPremiumManager.activeProduct } returns activeProductFlow

        viewModel = PaywallViewModel(mockApp)
    }

    // --- Initial state ---

    @Test
    fun `initial selectedPlanId is PRODUCT_YEARLY`() = runTest {
        viewModel.selectedPlanId.test {
            assertEquals(BillingManager.PRODUCT_YEARLY, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial isPremium reflects premiumManager state`() = runTest {
        viewModel.isPremium.test {
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isPremium updates when premiumManager emits true`() = runTest {
        viewModel.isPremium.test {
            assertFalse(awaitItem())
            isPremiumFlow.value = true
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial billingState is Disconnected`() = runTest {
        viewModel.billingState.test {
            assertEquals(BillingState.Disconnected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `billingState reflects billingManager emissions`() = runTest {
        viewModel.billingState.test {
            assertEquals(BillingState.Disconnected, awaitItem())
            billingStateFlow.value = BillingState.Connected
            assertEquals(BillingState.Connected, awaitItem())
            billingStateFlow.value = BillingState.PurchaseSuccess
            assertEquals(BillingState.PurchaseSuccess, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial activeProduct is null`() = runTest {
        viewModel.activeProduct.test {
            assertEquals(null, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- selectPlan ---

    @Test
    fun `selectPlan changes selectedPlanId`() = runTest {
        viewModel.selectPlan(BillingManager.PRODUCT_WEEKLY)

        viewModel.selectedPlanId.test {
            assertEquals(BillingManager.PRODUCT_WEEKLY, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selectPlan to monthly changes selectedPlanId`() = runTest {
        viewModel.selectPlan(BillingManager.PRODUCT_MONTHLY)

        viewModel.selectedPlanId.test {
            assertEquals(BillingManager.PRODUCT_MONTHLY, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selectPlan to lifetime changes selectedPlanId`() = runTest {
        viewModel.selectPlan(BillingManager.PRODUCT_LIFETIME)

        viewModel.selectedPlanId.test {
            assertEquals(BillingManager.PRODUCT_LIFETIME, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- default plan options ---

    @Test
    fun `planOptions has 4 plans by default`() = runTest {
        viewModel.planOptions.test {
            val options = awaitItem()
            assertEquals(4, options.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `planOptions contains weekly monthly yearly lifetime plans`() = runTest {
        viewModel.planOptions.test {
            val options = awaitItem()
            val productIds = options.map { it.productId }
            assertTrue(productIds.contains(BillingManager.PRODUCT_WEEKLY))
            assertTrue(productIds.contains(BillingManager.PRODUCT_MONTHLY))
            assertTrue(productIds.contains(BillingManager.PRODUCT_YEARLY))
            assertTrue(productIds.contains(BillingManager.PRODUCT_LIFETIME))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `yearly plan is marked as popular`() = runTest {
        viewModel.planOptions.test {
            val options = awaitItem()
            val yearly = options.find { it.productId == BillingManager.PRODUCT_YEARLY }
            assertTrue(yearly?.isPopular == true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `non-yearly plans are not marked as popular`() = runTest {
        viewModel.planOptions.test {
            val options = awaitItem()
            val nonYearly = options.filter { it.productId != BillingManager.PRODUCT_YEARLY }
            assertTrue(nonYearly.all { !it.isPopular })
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- purchaseSelectedPlan ---

    @Test
    fun `purchaseSelectedPlan does nothing when productDetails is null`() = runTest {
        val mockActivity = mockk<android.app.Activity>()
        // products list is empty, so productDetails will be null
        viewModel.purchaseSelectedPlan(mockActivity)

        verify(exactly = 0) { mockBillingManager.launchPurchaseFlow(any(), any()) }
    }

    // --- restorePurchases ---

    @Test
    fun `restorePurchases calls billingManager restorePurchases`() = runTest {
        viewModel.restorePurchases()

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        io.mockk.coVerify { mockBillingManager.restorePurchases() }
    }

    // --- resetBillingState ---

    @Test
    fun `resetBillingState calls billingManager startConnection`() {
        viewModel.resetBillingState()

        verify { mockBillingManager.startConnection() }
    }
}
