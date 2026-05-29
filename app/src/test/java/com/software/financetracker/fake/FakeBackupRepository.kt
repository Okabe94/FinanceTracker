package com.software.financetracker.fake

import com.software.financetracker.core.backup.BackupData
import com.software.financetracker.core.backup.BackupPreferences
import com.software.financetracker.core.backup.BackupRepository

class FakeBackupRepository : BackupRepository {

    var shouldThrowOnExport = false
    var shouldThrowOnImport = false
    var importedData: BackupData? = null

    private val defaultPreferences = BackupPreferences(
        notificationsEnabled = true,
        themeMode = "DARK",
        defaultCurrency = "COP",
        useCustomExchangeRates = false,
        customUsdRate = 0f,
        customEurRate = 0f,
        customGbpRate = 0f,
        investmentSortField = "ALPHABETICAL",
        investmentSortDirection = "ASC",
        homeSortField = "ALPHABETICAL",
        homeSortDirection = "ASC"
    )

    val sampleBackupData = BackupData(
        exportedAt = "2026-05-28 10:00",
        preferences = defaultPreferences,
        categories = emptyList(),
        expenses = emptyList(),
        recurringExpenses = emptyList(),
        income = emptyList(),
        recurringIncome = emptyList(),
        investments = emptyList(),
        investmentEntries = emptyList(),
        exchangeRates = emptyList(),
        goals = emptyList()
    )

    override suspend fun export(): BackupData {
        if (shouldThrowOnExport) throw RuntimeException("Export failed")
        return sampleBackupData
    }

    override suspend fun import(data: BackupData) {
        if (shouldThrowOnImport) throw RuntimeException("Import failed")
        importedData = data
    }
}
