package com.software.financetracker.feature.income.recurring.list

sealed interface RecurringIncomeListEvent {
    data object NavigateBack : RecurringIncomeListEvent
    data object NavigateToAddForm : RecurringIncomeListEvent
    data class NavigateToEditForm(val templateId: Long) : RecurringIncomeListEvent
}
