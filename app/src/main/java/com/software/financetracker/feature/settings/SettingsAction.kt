package com.software.financetracker.feature.settings

import com.software.financetracker.feature.home.HomeSortField
import com.software.financetracker.feature.investment.list.SortDirection
import com.software.financetracker.feature.investment.list.SortField
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
    data class OnInvestmentSortFieldSelected(val field: SortField) : SettingsAction
    data object OnInvestmentSortDropdownToggle : SettingsAction
    data object OnInvestmentSortDropdownDismiss : SettingsAction
    data class OnInvestmentSortDirectionChange(val direction: SortDirection) : SettingsAction
    data class OnHomeSortFieldSelected(val field: HomeSortField) : SettingsAction
    data object OnHomeSortDropdownToggle : SettingsAction
    data object OnHomeSortDropdownDismiss : SettingsAction
    data class OnHomeSortDirectionChange(val direction: SortDirection) : SettingsAction
}
