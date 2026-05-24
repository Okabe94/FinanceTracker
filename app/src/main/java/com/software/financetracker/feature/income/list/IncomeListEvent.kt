package com.software.financetracker.feature.income.list

sealed interface IncomeListEvent {
    data object NavigateBack : IncomeListEvent
    data object NavigateToAddIncome : IncomeListEvent
    data class NavigateToEditIncome(val incomeId: Long) : IncomeListEvent
    data class NavigateToEditRecurringIncome(val id: Long) : IncomeListEvent
}
