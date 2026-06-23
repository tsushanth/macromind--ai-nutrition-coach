package com.factory.macromindainutritioncoach.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class BillingState {
    object Disconnected : BillingState()
    object Connected : BillingState()
    object PurchaseSuccess : BillingState()
    object PurchaseCancelled : BillingState()
    object PurchasePending : BillingState()
    object Restoring : BillingState()
    object RestoreComplete : BillingState()
    data class Error(val message: String) : BillingState()
}

class BillingManager(
    private val context: Context,
    private val premiumManager: PremiumManager
) {

    companion object {
        const val PRODUCT_WEEKLY = "com.factory.macromindainutritioncoach.subscription.weekly"
        const val PRODUCT_MONTHLY = "com.factory.macromindainutritioncoach.subscription.monthly"
        const val PRODUCT_YEARLY = "com.factory.macromindainutritioncoach.subscription.yearly"
        const val PRODUCT_LIFETIME = "com.factory.macromindainutritioncoach.subscription.lifetime"
        const val PRODUCT_REMOVE_ADS = "com.factory.macromindainutritioncoach.remove_ads"

        val SUBSCRIPTION_PRODUCT_IDS = listOf(PRODUCT_WEEKLY, PRODUCT_MONTHLY, PRODUCT_YEARLY)
        val INAPP_PRODUCT_IDS = listOf(PRODUCT_LIFETIME, PRODUCT_REMOVE_ADS)
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _billingState = MutableStateFlow<BillingState>(BillingState.Disconnected)
    val billingState: StateFlow<BillingState> = _billingState.asStateFlow()

    private val _products = MutableStateFlow<List<ProductDetails>>(emptyList())
    val products: StateFlow<List<ProductDetails>> = _products.asStateFlow()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { result, purchases ->
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    scope.launch { handlePurchase(purchase) }
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                _billingState.value = BillingState.PurchaseCancelled
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                scope.launch { restorePurchases() }
            }
            else -> {
                _billingState.value = BillingState.Error(result.debugMessage)
            }
        }
    }

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    fun startConnection() {
        if (billingClient.isReady) {
            scope.launch {
                loadProducts()
                restorePurchases()
            }
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _billingState.value = BillingState.Connected
                    scope.launch {
                        loadProducts()
                        restorePurchases()
                    }
                } else {
                    _billingState.value = BillingState.Error(billingResult.debugMessage)
                }
            }

            override fun onBillingServiceDisconnected() {
                _billingState.value = BillingState.Disconnected
                startConnection()
            }
        })
    }

    fun endConnection() {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    private suspend fun loadProducts() {
        val allProducts = mutableListOf<ProductDetails>()

        val subParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                SUBSCRIPTION_PRODUCT_IDS.map { productId ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                }
            )
            .build()

        val subResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(subParams)
        }
        if (subResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            subResult.productDetailsList?.let { allProducts.addAll(it) }
        }

        val inappParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                INAPP_PRODUCT_IDS.map { productId ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                }
            )
            .build()

        val inappResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(inappParams)
        }
        if (inappResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            inappResult.productDetailsList?.let { allProducts.addAll(it) }
        }

        _products.value = allProducts
    }

    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        val productDetailsParamsList = if (productDetails.productType == BillingClient.ProductType.SUBS) {
            val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
                ?: run {
                    _billingState.value = BillingState.Error("No offer available for this plan.")
                    return
                }
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerToken)
                    .build()
            )
        } else {
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .build()
            )
        }

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            _billingState.value = BillingState.Error(result.debugMessage)
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                withContext(Dispatchers.IO) {
                    billingClient.acknowledgePurchase(acknowledgeParams)
                }
            }
            premiumManager.grantPremiumAccess(purchase.products)
            _billingState.value = BillingState.PurchaseSuccess
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            _billingState.value = BillingState.PurchasePending
        }
    }

    suspend fun restorePurchases() {
        if (!billingClient.isReady) return
        _billingState.value = BillingState.Restoring

        val subParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        val subResult = withContext(Dispatchers.IO) {
            billingClient.queryPurchasesAsync(subParams)
        }
        if (subResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            subResult.purchasesList
                .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                .forEach { purchase ->
                    if (!purchase.isAcknowledged) {
                        val ackParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                            .build()
                        withContext(Dispatchers.IO) { billingClient.acknowledgePurchase(ackParams) }
                    }
                    premiumManager.grantPremiumAccess(purchase.products)
                }
        }

        val inappParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        val inappResult = withContext(Dispatchers.IO) {
            billingClient.queryPurchasesAsync(inappParams)
        }
        if (inappResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            inappResult.purchasesList
                .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                .forEach { purchase ->
                    premiumManager.grantPremiumAccess(purchase.products)
                }
        }

        _billingState.value = BillingState.RestoreComplete
    }

    fun getProductDetails(productId: String): ProductDetails? =
        _products.value.find { it.productId == productId }
}
