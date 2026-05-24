package com.software.financetracker.core.preferences

import com.software.financetracker.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

interface UserPreferences {
    val notificationsEnabled: Flow<Boolean>
    val themeMode: Flow<ThemeMode>
    val defaultCurrency: Flow<String>
    val useCustomExchangeRates: Flow<Boolean>
    val customUsdRate: Flow<Float>
    val customEurRate: Flow<Float>
    val customGbpRate: Flow<Float>

    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setDefaultCurrency(currency: String)
    suspend fun setUseCustomExchangeRates(enabled: Boolean)
    suspend fun setCustomRates(usd: Float, eur: Float, gbp: Float)
}
