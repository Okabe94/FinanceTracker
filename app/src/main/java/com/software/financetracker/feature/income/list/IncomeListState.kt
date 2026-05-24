package com.software.financetracker.feature.income.list

data class IncomeListState(
    val recurringTemplates: List<RecurringIncomeTemplateUi> = emptyList(),
    val items: List<IncomeItem.Entry> = emptyList(),
    val isLoading: Boolean = true
)
