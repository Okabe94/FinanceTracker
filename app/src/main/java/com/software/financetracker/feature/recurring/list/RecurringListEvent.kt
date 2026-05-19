package com.software.financetracker.feature.recurring.list

sealed interface RecurringListEvent {
    data object NavigateBack : RecurringListEvent
    data object NavigateToAddForm : RecurringListEvent
    data class NavigateToEditForm(val templateId: Long) : RecurringListEvent
}
