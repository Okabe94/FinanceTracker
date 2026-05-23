package com.software.financetracker.data.di

import androidx.room.Room
import com.software.financetracker.core.preferences.UserPreferencesRepository
import com.software.financetracker.data.local.FinanceDatabase
import com.software.financetracker.data.repository.CategoryRepositoryImpl
import com.software.financetracker.data.repository.ExpenseRepositoryImpl
import com.software.financetracker.data.remote.ExchangeRateRemoteDataSource
import com.software.financetracker.data.repository.ExchangeRateRepositoryImpl
import com.software.financetracker.data.repository.GoalRepositoryImpl
import com.software.financetracker.data.repository.IncomeRepositoryImpl
import com.software.financetracker.data.repository.InvestmentEntryRepositoryImpl
import com.software.financetracker.data.repository.InvestmentRepositoryImpl
import com.software.financetracker.data.repository.RecurringExpenseRepositoryImpl
import com.software.financetracker.data.repository.RecurringIncomeRepositoryImpl
import com.software.financetracker.domain.repository.CategoryRepository
import com.software.financetracker.domain.repository.ExchangeRateRepository
import com.software.financetracker.domain.repository.ExpenseRepository
import com.software.financetracker.domain.repository.GoalRepository
import com.software.financetracker.domain.repository.IncomeRepository
import com.software.financetracker.domain.repository.InvestmentEntryRepository
import com.software.financetracker.domain.repository.InvestmentRepository
import com.software.financetracker.domain.repository.RecurringExpenseRepository
import com.software.financetracker.domain.repository.RecurringIncomeRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = FinanceDatabase::class.java,
            name = "finance_tracker.db"
        ).addMigrations(
            FinanceDatabase.MIGRATION_1_2,
            FinanceDatabase.MIGRATION_2_3,
            FinanceDatabase.MIGRATION_3_4,
            FinanceDatabase.MIGRATION_4_5,
            FinanceDatabase.MIGRATION_5_6,
            FinanceDatabase.MIGRATION_6_7,
            FinanceDatabase.MIGRATION_7_8
        ).build()
    }
    single { get<FinanceDatabase>().categoryDao() }
    single { get<FinanceDatabase>().expenseDao() }
    single { get<FinanceDatabase>().notificationStateDao() }
    single { get<FinanceDatabase>().recurringExpenseDao() }
    single { get<FinanceDatabase>().investmentDao() }
    single { get<FinanceDatabase>().investmentEntryDao() }
    single { get<FinanceDatabase>().exchangeRateDao() }
    single { get<FinanceDatabase>().incomeDao() }
    single { get<FinanceDatabase>().recurringIncomeDao() }
    single { get<FinanceDatabase>().goalDao() }
    single { ExchangeRateRemoteDataSource() }
    single<ExchangeRateRepository> { ExchangeRateRepositoryImpl(get(), get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<ExpenseRepository> { ExpenseRepositoryImpl(get(), androidContext()) }
    single<RecurringExpenseRepository> { RecurringExpenseRepositoryImpl(get()) }
    single<InvestmentRepository> { InvestmentRepositoryImpl(get()) }
    single<InvestmentEntryRepository> { InvestmentEntryRepositoryImpl(get()) }
    single<IncomeRepository> { IncomeRepositoryImpl(get()) }
    single<RecurringIncomeRepository> { RecurringIncomeRepositoryImpl(get()) }
    single<GoalRepository> { GoalRepositoryImpl(get()) }
    single { UserPreferencesRepository(androidContext()) }
}
