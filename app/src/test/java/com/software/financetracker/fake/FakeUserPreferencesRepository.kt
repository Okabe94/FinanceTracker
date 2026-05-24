package com.software.financetracker.fake

import com.software.financetracker.core.preferences.UserPreferences
import com.software.financetracker.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserPreferencesRepository : UserPreferences {
    var defaultCurrencyValue = "COP"

    override val notificationsEnabled: Flow<Boolean> = MutableStateFlow(true)
    override val themeMode: Flow<ThemeMode> = MutableStateFlow(ThemeMode.DARK)
    override val defaultCurrency: Flow<String> = MutableStateFlow(defaultCurrencyValue)
    override val useCustomExchangeRates: Flow<Boolean> = MutableStateFlow(false)
    override val customUsdRate: Flow<Float> = MutableStateFlow(0f)
    override val customEurRate: Flow<Float> = MutableStateFlow(0f)
    override val customGbpRate: Flow<Float> = MutableStateFlow(0f)

    override suspend fun setNotificationsEnabled(enabled: Boolean) {}
    override suspend fun setThemeMode(mode: ThemeMode) {}
    override suspend fun setDefaultCurrency(currency: String) {}
    override suspend fun setUseCustomExchangeRates(enabled: Boolean) {}
    override suspend fun setCustomRates(usd: Float, eur: Float, gbp: Float) {}
}
