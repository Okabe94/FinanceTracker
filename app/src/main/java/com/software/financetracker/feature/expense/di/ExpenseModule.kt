package com.software.financetracker.feature.expense.di

import com.software.financetracker.feature.expense.assistant.AssistantExpenseViewModel
import com.software.financetracker.feature.expense.form.ExpenseFormViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val expenseModule = module {
    viewModel { ExpenseFormViewModel(get(), get(), get(), get()) }
    viewModel { AssistantExpenseViewModel(get(), get(), get()) }
}
