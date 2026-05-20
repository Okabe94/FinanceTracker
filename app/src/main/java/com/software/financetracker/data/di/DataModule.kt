package com.software.financetracker.data.di

import androidx.room.Room
import com.software.financetracker.core.preferences.UserPreferencesRepository
import com.software.financetracker.data.local.FinanceDatabase
import com.software.financetracker.data.repository.CategoryRepositoryImpl
import com.software.financetracker.data.repository.ExpenseRepositoryImpl
import com.software.financetracker.data.repository.InvestmentEntryRepositoryImpl
import com.software.financetracker.data.repository.InvestmentRepositoryImpl
import com.software.financetracker.data.repository.RecurringExpenseRepositoryImpl
import com.software.financetracker.domain.repository.CategoryRepository
import com.software.financetracker.domain.repository.ExpenseRepository
import com.software.financetracker.domain.repository.InvestmentEntryRepository
import com.software.financetracker.domain.repository.InvestmentRepository
import com.software.financetracker.domain.repository.RecurringExpenseRepository
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
            FinanceDatabase.MIGRATION_3_4
        ).build()
    }
    single { get<FinanceDatabase>().categoryDao() }
    single { get<FinanceDatabase>().expenseDao() }
    single { get<FinanceDatabase>().notificationStateDao() }
    single { get<FinanceDatabase>().recurringExpenseDao() }
    single { get<FinanceDatabase>().investmentDao() }
    single { get<FinanceDatabase>().investmentEntryDao() }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<ExpenseRepository> { ExpenseRepositoryImpl(get(), androidContext()) }
    single<RecurringExpenseRepository> { RecurringExpenseRepositoryImpl(get()) }
    single<InvestmentRepository> { InvestmentRepositoryImpl(get()) }
    single<InvestmentEntryRepository> { InvestmentEntryRepositoryImpl(get()) }
    single { UserPreferencesRepository(androidContext()) }
}
