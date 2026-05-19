package com.software.financetracker.feature.recurring.di

import com.software.financetracker.feature.recurring.form.RecurringExpenseFormViewModel
import com.software.financetracker.feature.recurring.list.RecurringListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val recurringModule = module {
    viewModel { RecurringExpenseFormViewModel(get(), get(), get()) }
    viewModel { RecurringListViewModel(get(), get()) }
}
