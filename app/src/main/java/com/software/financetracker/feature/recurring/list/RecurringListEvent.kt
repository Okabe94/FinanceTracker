package com.software.financetracker.feature.recurring.list

sealed interface RecurringListEvent {
    data object NavigateBack : RecurringListEvent
    data object NavigateToAddTemplate : RecurringListEvent
    data class NavigateToAddExpense(val categoryId: Long) : RecurringListEvent
    data class NavigateToEditForm(val templateId: Long) : RecurringListEvent
}
