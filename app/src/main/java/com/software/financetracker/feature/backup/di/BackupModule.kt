package com.software.financetracker.feature.backup.di

import androidx.room.withTransaction
import com.software.financetracker.core.backup.BackupFileHelper
import com.software.financetracker.core.backup.BackupFileOps
import com.software.financetracker.core.backup.BackupRepository
import com.software.financetracker.core.backup.BackupRepositoryImpl
import com.software.financetracker.data.local.FinanceDatabase
import com.software.financetracker.feature.backup.BackupViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val backupModule = module {
    single<BackupFileOps> { BackupFileHelper(androidContext()) }
    single<BackupRepository> {
        val db = get<FinanceDatabase>()
        BackupRepositoryImpl(
            runInTransaction = { block -> db.withTransaction { block() } },
            categoryDao = get(),
            expenseDao = get(),
            recurringExpenseDao = get(),
            incomeDao = get(),
            recurringIncomeDao = get(),
            investmentDao = get(),
            investmentEntryDao = get(),
            exchangeRateDao = get(),
            goalDao = get(),
            prefs = get()
        )
    }
    viewModel { BackupViewModel(get(), get()) }
}
