package com.factory.macromindainutritioncoach.billing

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PremiumManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "macromind_premium"
        private const val KEY_IS_PREMIUM = "is_premium"
        private const val KEY_ADS_REMOVED = "ads_removed"
        private const val KEY_ACTIVE_PRODUCT = "active_product"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _isPremium = MutableStateFlow(prefs.getBoolean(KEY_IS_PREMIUM, false))
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _adsRemoved = MutableStateFlow(prefs.getBoolean(KEY_ADS_REMOVED, false))
    val adsRemoved: StateFlow<Boolean> = _adsRemoved.asStateFlow()

    private val _activeProduct = MutableStateFlow(prefs.getString(KEY_ACTIVE_PRODUCT, null))
    val activeProduct: StateFlow<String?> = _activeProduct.asStateFlow()

    fun grantPremiumAccess(productIds: List<String>) {
        val hasPremiumProduct = productIds.any { it in listOf(
            BillingManager.PRODUCT_WEEKLY,
            BillingManager.PRODUCT_MONTHLY,
            BillingManager.PRODUCT_YEARLY,
            BillingManager.PRODUCT_LIFETIME
        )}
        val hasAdsRemoval = productIds.any { it == BillingManager.PRODUCT_REMOVE_ADS }

        if (hasPremiumProduct) {
            val activeProduct = productIds.firstOrNull { it != BillingManager.PRODUCT_REMOVE_ADS }
            prefs.edit()
                .putBoolean(KEY_IS_PREMIUM, true)
                .putString(KEY_ACTIVE_PRODUCT, activeProduct)
                .apply()
            _isPremium.value = true
            _activeProduct.value = activeProduct
        }

        if (hasAdsRemoval) {
            prefs.edit().putBoolean(KEY_ADS_REMOVED, true).apply()
            _adsRemoved.value = true
        }
    }

    fun revokePremiumAccess() {
        prefs.edit()
            .putBoolean(KEY_IS_PREMIUM, false)
            .putString(KEY_ACTIVE_PRODUCT, null)
            .apply()
        _isPremium.value = false
        _activeProduct.value = null
    }

    fun activeProductLabel(): String? = when (_activeProduct.value) {
        BillingManager.PRODUCT_WEEKLY -> "Weekly"
        BillingManager.PRODUCT_MONTHLY -> "Monthly"
        BillingManager.PRODUCT_YEARLY -> "Yearly"
        BillingManager.PRODUCT_LIFETIME -> "Lifetime"
        else -> null
    }
}
