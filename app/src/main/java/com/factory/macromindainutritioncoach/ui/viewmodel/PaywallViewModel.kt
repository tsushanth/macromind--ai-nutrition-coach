package com.factory.macromindainutritioncoach.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.factory.macromindainutritioncoach.MacroMindApplication
import com.factory.macromindainutritioncoach.billing.BillingManager
import com.factory.macromindainutritioncoach.billing.BillingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PlanOption(
    val productId: String,
    val title: String,
    val price: String,
    val period: String,
    val description: String,
    val isPopular: Boolean = false,
    val productDetails: ProductDetails? = null
)

class PaywallViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as MacroMindApplication
    private val billingManager = app.billingManager
    private val premiumManager = app.premiumManager

    val isPremium: StateFlow<Boolean> = premiumManager.isPremium
    val billingState: StateFlow<BillingState> = billingManager.billingState
    val activeProduct: StateFlow<String?> = premiumManager.activeProduct

    private val _selectedPlanId = MutableStateFlow(BillingManager.PRODUCT_YEARLY)
    val selectedPlanId: StateFlow<String> = _selectedPlanId.asStateFlow()

    val planOptions: StateFlow<List<PlanOption>> = billingManager.products
        .map { productList -> buildPlanOptions(productList) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), buildDefaultPlanOptions())

    private fun buildPlanOptions(productDetails: List<ProductDetails>): List<PlanOption> {
        fun priceFor(productId: String): String {
            val detail = productDetails.find { it.productId == productId } ?: return fallbackPrice(productId)
            return detail.subscriptionOfferDetails
                ?.firstOrNull()
                ?.pricingPhases
                ?.pricingPhaseList
                ?.lastOrNull()
                ?.formattedPrice
                ?: detail.oneTimePurchaseOfferDetails?.formattedPrice
                ?: fallbackPrice(productId)
        }

        return listOf(
            PlanOption(
                productId = BillingManager.PRODUCT_WEEKLY,
                title = "Weekly",
                price = priceFor(BillingManager.PRODUCT_WEEKLY),
                period = "/ week",
                description = "Try premium, cancel anytime",
                productDetails = productDetails.find { it.productId == BillingManager.PRODUCT_WEEKLY }
            ),
            PlanOption(
                productId = BillingManager.PRODUCT_MONTHLY,
                title = "Monthly",
                price = priceFor(BillingManager.PRODUCT_MONTHLY),
                period = "/ month",
                description = "Flexible monthly billing",
                productDetails = productDetails.find { it.productId == BillingManager.PRODUCT_MONTHLY }
            ),
            PlanOption(
                productId = BillingManager.PRODUCT_YEARLY,
                title = "Yearly",
                price = priceFor(BillingManager.PRODUCT_YEARLY),
                period = "/ year",
                description = "Best value — save over 50%",
                isPopular = true,
                productDetails = productDetails.find { it.productId == BillingManager.PRODUCT_YEARLY }
            ),
            PlanOption(
                productId = BillingManager.PRODUCT_LIFETIME,
                title = "Lifetime",
                price = priceFor(BillingManager.PRODUCT_LIFETIME),
                period = "one-time",
                description = "Pay once, own forever",
                productDetails = productDetails.find { it.productId == BillingManager.PRODUCT_LIFETIME }
            )
        )
    }

    private fun buildDefaultPlanOptions(): List<PlanOption> = listOf(
        PlanOption(BillingManager.PRODUCT_WEEKLY, "Weekly", "$2.23", "/ week", "Try premium, cancel anytime"),
        PlanOption(BillingManager.PRODUCT_MONTHLY, "Monthly", "$5.59", "/ month", "Flexible monthly billing"),
        PlanOption(BillingManager.PRODUCT_YEARLY, "Yearly", "$33.54", "/ year", "Best value — save over 50%", isPopular = true),
        PlanOption(BillingManager.PRODUCT_LIFETIME, "Lifetime", "$67.08", "one-time", "Pay once, own forever")
    )

    private fun fallbackPrice(productId: String): String = when (productId) {
        BillingManager.PRODUCT_WEEKLY -> "$2.23"
        BillingManager.PRODUCT_MONTHLY -> "$5.59"
        BillingManager.PRODUCT_YEARLY -> "$33.54"
        BillingManager.PRODUCT_LIFETIME -> "$67.08"
        BillingManager.PRODUCT_REMOVE_ADS -> "$1.99"
        else -> "—"
    }

    fun selectPlan(productId: String) {
        _selectedPlanId.value = productId
    }

    fun purchaseSelectedPlan(activity: Activity) {
        val selectedId = _selectedPlanId.value
        val plan = planOptions.value.find { it.productId == selectedId }
        val productDetails = plan?.productDetails
        if (productDetails != null) {
            billingManager.launchPurchaseFlow(activity, productDetails)
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            billingManager.restorePurchases()
        }
    }

    fun resetBillingState() {
        // Re-connect to reset the billing state to Connected if previously in a terminal state
        billingManager.startConnection()
    }
}
