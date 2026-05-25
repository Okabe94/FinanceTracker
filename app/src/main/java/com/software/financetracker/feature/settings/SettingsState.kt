package com.software.financetracker.feature.settings

import com.software.financetracker.feature.home.HomeSortField
import com.software.financetracker.feature.investment.list.SortDirection
import com.software.financetracker.feature.investment.list.SortField
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
    val isSavingRates: Boolean = false,
    val investmentSortField: SortField = SortField.ALPHABETICAL,
    val investmentSortDirection: SortDirection = SortDirection.ASC,
    val showInvestmentSortDropdown: Boolean = false,
    val homeSortField: HomeSortField = HomeSortField.ALPHABETICAL,
    val homeSortDirection: SortDirection = SortDirection.ASC,
    val showHomeSortDropdown: Boolean = false
)
