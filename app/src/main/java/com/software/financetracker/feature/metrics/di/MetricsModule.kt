package com.software.financetracker.feature.metrics.di

import com.software.financetracker.feature.metrics.MetricsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val metricsModule = module {
    viewModelOf(::MetricsViewModel)
}
