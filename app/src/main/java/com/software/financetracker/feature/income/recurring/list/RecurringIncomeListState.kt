package com.software.financetracker.feature.income.recurring.list

data class RecurringIncomeListState(
    val templates: List<RecurringIncomeTemplateUi> = emptyList(),
    val isLoading: Boolean = true
)
