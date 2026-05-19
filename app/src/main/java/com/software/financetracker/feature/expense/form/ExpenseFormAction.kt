package com.software.financetracker.feature.expense.form

sealed interface ExpenseFormAction {
    data object OnBackClick : ExpenseFormAction
    data class OnAmountChange(val value: String) : ExpenseFormAction
    data class OnDescriptionChange(val value: String) : ExpenseFormAction
    data object OnDateFieldClick : ExpenseFormAction
    data class OnDateSelected(val epochMillis: Long) : ExpenseFormAction
    data object OnDatePickerDismiss : ExpenseFormAction
    data object OnSaveClick : ExpenseFormAction
    data object OnDeleteClick : ExpenseFormAction
    data object OnDeleteConfirm : ExpenseFormAction
    data object OnDeleteDismiss : ExpenseFormAction
}
