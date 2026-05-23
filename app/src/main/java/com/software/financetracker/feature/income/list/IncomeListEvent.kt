package com.software.financetracker.feature.income.list

sealed interface IncomeListEvent {
    data object NavigateBack : IncomeListEvent
    data object NavigateToAddIncome : IncomeListEvent
    data class NavigateToEditIncome(val incomeId: Long) : IncomeListEvent
    data object NavigateToAddTemplate : IncomeListEvent
    data class NavigateToEditTemplate(val templateId: Long) : IncomeListEvent
}
