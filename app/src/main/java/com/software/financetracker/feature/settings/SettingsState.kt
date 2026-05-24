package com.software.financetracker.feature.settings

import com.software.financetracker.ui.theme.ThemeMode

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val defaultCurrency: String = "COP",
    val showCurrencyDropdown: Boolean = false,
    val useCustomExchangeRates: Boolean = false,
    val customUsdRate: String = "",
    val customEurRate: String = "",
    val customGbpRate: String = "",
    val customUsdRateError: String? = null,
    val customEurRateError: String? = null,
    val customGbpRateError: String? = null,
    val notificationsEnabled: Boolean = true,
    val isLoading: Boolean = true,
    val isSavingRates: Boolean = false
)
