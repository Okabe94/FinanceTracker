package com.software.financetracker.feature.category.di

import com.software.financetracker.feature.category.detail.CategoryDetailViewModel
import com.software.financetracker.feature.category.form.CategoryFormViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val categoryModule = module {
    viewModelOf(::CategoryFormViewModel)
    viewModelOf(::CategoryDetailViewModel)
}
