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

    val investmentSortField: Flow<String>
    val investmentSortDirection: Flow<String>
    val homeSortField: Flow<String>
    val homeSortDirection: Flow<String>

    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setDefaultCurrency(currency: String)
    suspend fun setUseCustomExchangeRates(enabled: Boolean)
    suspend fun setCustomRates(usd: Float, eur: Float, gbp: Float)
    suspend fun setInvestmentSort(field: String, direction: String)
    suspend fun setHomeSort(field: String, direction: String)
}
