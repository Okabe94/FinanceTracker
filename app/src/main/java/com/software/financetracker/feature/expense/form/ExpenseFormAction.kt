package com.software.financetracker.feature.expense.form

import com.software.financetracker.domain.model.RecurrenceType

sealed interface ExpenseFormAction {
    data object OnBackClick : ExpenseFormAction
    data class OnAmountChange(val value: String) : ExpenseFormAction
    data class OnDescriptionChange(val value: String) : ExpenseFormAction
    data object OnDateFieldClick : ExpenseFormAction
    data class OnDateSelected(val epochMillis: Long) : ExpenseFormAction
    data object OnDatePickerDismiss : ExpenseFormAction
    data object OnToggleRecurring : ExpenseFormAction
    data class OnRecurrenceTypeSelect(val type: RecurrenceType) : ExpenseFormAction
    data class OnCustomIntervalChange(val days: String) : ExpenseFormAction
    data class OnCategorySelect(val id: Long) : ExpenseFormAction
    data object OnToggleCategoryDropdown : ExpenseFormAction
    data object OnCategoryDropdownDismiss : ExpenseFormAction
    data object OnToggleActive : ExpenseFormAction
    data object OnSaveClick : ExpenseFormAction
    data object OnDeleteClick : ExpenseFormAction
    data object OnDeleteConfirm : ExpenseFormAction
    data object OnDeleteDismiss : ExpenseFormAction
}
