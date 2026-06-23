package com.factory.macromindainutritioncoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.factory.macromindainutritioncoach.navigation.MacroMindNavigation
import com.factory.macromindainutritioncoach.ui.theme.MacroMindTheme

class MainActivity : ComponentActivity() {

    private val billingManager get() = (application as MacroMindApplication).billingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        billingManager.startConnection()

        setContent {
            MacroMindTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MacroMindNavigation()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.endConnection()
    }
}
