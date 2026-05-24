package com.software.financetracker.feature.investment.di

import com.software.financetracker.feature.investment.detail.InvestmentDetailViewModel
import com.software.financetracker.feature.investment.entry.InvestmentEntryFormViewModel
import com.software.financetracker.feature.investment.form.InvestmentFormViewModel
import com.software.financetracker.feature.investment.list.InvestmentListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val investmentModule = module {
    viewModel { InvestmentListViewModel(get(), get(), get()) }
    viewModel { params -> InvestmentDetailViewModel(params.get(), get(), get()) }
    viewModel { params -> InvestmentFormViewModel(params.get(), get(), get()) }
    viewModel { params -> InvestmentEntryFormViewModel(params.get(), get(), get()) }
}
