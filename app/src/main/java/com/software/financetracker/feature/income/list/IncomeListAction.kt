package com.software.financetracker.feature.income.list

sealed interface IncomeListAction {
    data object OnBackClick : IncomeListAction
    data object OnAddIncomeClick : IncomeListAction
    data class OnEntryClick(val incomeId: Long) : IncomeListAction
    data class OnTemplateClick(val templateId: Long) : IncomeListAction
    data class OnDeleteClick(val incomeId: Long) : IncomeListAction
}
