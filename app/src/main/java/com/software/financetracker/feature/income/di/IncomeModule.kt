package com.software.financetracker.feature.income.di

import com.software.financetracker.feature.income.form.IncomeFormViewModel
import com.software.financetracker.feature.income.list.IncomeListViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val incomeModule = module {
    viewModel { IncomeFormViewModel(get(), get(), get()) }
    viewModel { IncomeListViewModel(get(), get()) }
}
