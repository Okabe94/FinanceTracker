package com.software.financetracker.feature.goal.di

import com.software.financetracker.feature.goal.detail.GoalDetailViewModel
import com.software.financetracker.feature.goal.form.GoalFormViewModel
import com.software.financetracker.feature.goal.list.GoalListViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val goalModule = module {
    viewModel { GoalFormViewModel(get(), get()) }
    viewModel { GoalListViewModel(get()) }
    viewModel { GoalDetailViewModel(get(), get()) }
}
