package com.software.financetracker.feature.settings

import com.software.financetracker.ui.theme.ThemeMode

sealed interface SettingsAction {
    data object OnBackClick : SettingsAction
    data class OnThemeModeChange(val mode: ThemeMode) : SettingsAction
    data class OnDefaultCurrencySelected(val currency: String) : SettingsAction
    data object OnCurrencyDropdownToggle : SettingsAction
    data object OnCurrencyDropdownDismiss : SettingsAction
    data class OnUseCustomRatesToggle(val enabled: Boolean) : SettingsAction
    data class OnCustomUsdRateChange(val value: String) : SettingsAction
    data class OnCustomEurRateChange(val value: String) : SettingsAction
    data class OnCustomGbpRateChange(val value: String) : SettingsAction
    data object OnSaveCustomRates : SettingsAction
    data class OnNotificationsToggle(val enabled: Boolean) : SettingsAction
}
