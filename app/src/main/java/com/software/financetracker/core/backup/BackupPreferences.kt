package com.software.financetracker.core.backup

import kotlinx.serialization.Serializable

@Serializable
data class BackupPreferences(
    val notificationsEnabled: Boolean,
    val themeMode: String,
    val defaultCurrency: String,
    val useCustomExchangeRates: Boolean,
    val customUsdRate: Float,
    val customEurRate: Float,
    val customGbpRate: Float,
    val investmentSortField: String,
    val investmentSortDirection: String,
    val homeSortField: String,
    val homeSortDirection: String
)
