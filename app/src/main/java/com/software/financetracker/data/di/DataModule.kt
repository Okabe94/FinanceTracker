package com.software.financetracker.data.di

import androidx.room.Room
import com.software.financetracker.core.preferences.UserPreferencesRepository
import com.software.financetracker.data.local.FinanceDatabase
import com.software.financetracker.data.repository.CategoryRepositoryImpl
import com.software.financetracker.data.repository.ExpenseRepositoryImpl
import com.software.financetracker.domain.repository.CategoryRepository
import com.software.financetracker.domain.repository.ExpenseRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = FinanceDatabase::class.java,
            name = "finance_tracker.db"
        ).addMigrations(FinanceDatabase.MIGRATION_1_2).build()
    }
    single { get<FinanceDatabase>().categoryDao() }
    single { get<FinanceDatabase>().expenseDao() }
    single { get<FinanceDatabase>().notificationStateDao() }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<ExpenseRepository> { ExpenseRepositoryImpl(get()) }
    single { UserPreferencesRepository(androidContext()) }
}
