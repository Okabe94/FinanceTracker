package com.software.financetracker.feature.income.list

data class IncomeListState(
    val items: List<IncomeItem> = emptyList(),
    val isLoading: Boolean = true
)
