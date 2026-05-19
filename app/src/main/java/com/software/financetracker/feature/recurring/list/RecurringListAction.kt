package com.software.financetracker.feature.recurring.list

sealed interface RecurringListAction {
    data object OnBackClick : RecurringListAction
    data object OnAddClick : RecurringListAction
    data class OnTemplateClick(val templateId: Long) : RecurringListAction
}
