package com.software.financetracker.feature.recurring.list

sealed interface RecurringListAction {
    data object OnBackClick : RecurringListAction
    data object OnAddTemplateClick : RecurringListAction
    data class OnAddExpenseClick(val categoryId: Long) : RecurringListAction
    data class OnTemplateClick(val templateId: Long) : RecurringListAction
}
