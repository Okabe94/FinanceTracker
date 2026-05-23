package com.software.financetracker.feature.income.di

import com.software.financetracker.feature.income.form.IncomeFormViewModel
import com.software.financetracker.feature.income.list.IncomeListViewModel
import com.software.financetracker.feature.income.recurring.form.RecurringIncomeFormViewModel
import com.software.financetracker.feature.income.recurring.list.RecurringIncomeListViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val incomeModule = module {
    viewModel { IncomeFormViewModel(get(), get()) }
    viewModel { IncomeListViewModel(get(), get()) }
    viewModel { RecurringIncomeFormViewModel(get(), get()) }
    viewModel { RecurringIncomeListViewModel(get()) }
}
