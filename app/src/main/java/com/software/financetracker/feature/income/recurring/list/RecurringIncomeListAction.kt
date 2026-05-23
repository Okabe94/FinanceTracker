package com.software.financetracker.feature.income.recurring.list

sealed interface RecurringIncomeListAction {
    data object OnBackClick : RecurringIncomeListAction
    data object OnAddClick : RecurringIncomeListAction
    data class OnTemplateClick(val templateId: Long) : RecurringIncomeListAction
}
