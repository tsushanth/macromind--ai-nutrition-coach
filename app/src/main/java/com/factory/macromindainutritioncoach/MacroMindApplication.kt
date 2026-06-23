package com.factory.macromindainutritioncoach

import android.app.Application
import com.factory.macromindainutritioncoach.billing.BillingManager
import com.factory.macromindainutritioncoach.billing.PremiumManager
import com.factory.macromindainutritioncoach.data.local.database.MacroMindDatabase
import com.factory.macromindainutritioncoach.data.repository.NutritionRepository

class MacroMindApplication : Application() {

    val database by lazy { MacroMindDatabase.getDatabase(this) }

    val repository by lazy {
        NutritionRepository(
            foodEntryDao = database.foodEntryDao(),
            userProfileDao = database.userProfileDao(),
            commonFoodDao = database.commonFoodDao()
        )
    }

    val premiumManager by lazy { PremiumManager(this) }

    val billingManager by lazy { BillingManager(this, premiumManager) }
}
