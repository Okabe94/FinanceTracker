package com.software.financetracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.software.financetracker.core.preferences.UserPreferences
import com.software.financetracker.navigation.RootNavGraph
import com.software.financetracker.ui.theme.FinanceTrackerTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val prefs: UserPreferences by inject()

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* result handled silently — worker no-ops if permission is denied */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()
        setContent {
            val themeMode by prefs.themeMode.collectAsStateWithLifecycle(initialValue = com.software.financetracker.ui.theme.ThemeMode.DARK)
            FinanceTrackerTheme(themeMode = themeMode) {
                val navController = rememberNavController()
                DisposableEffect(navController) {
                    val listener = Consumer<Intent> { navController.handleDeepLink(it) }
                    addOnNewIntentListener(listener)
                    onDispose { removeOnNewIntentListener(listener) }
                }
                RootNavGraph(navController = navController)
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
