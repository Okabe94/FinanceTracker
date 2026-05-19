package com.software.financetracker.feature.recurring.form

import com.software.financetracker.domain.model.RecurrenceType

sealed interface RecurringExpenseFormAction {
    data object OnBackClick : RecurringExpenseFormAction
    data class OnAmountChange(val value: String) : RecurringExpenseFormAction
    data class OnDescriptionChange(val value: String) : RecurringExpenseFormAction
    data class OnRecurrenceTypeChange(val type: RecurrenceType) : RecurringExpenseFormAction
    data object OnDateFieldClick : RecurringExpenseFormAction
    data class OnDateSelected(val epochMillis: Long) : RecurringExpenseFormAction
    data object OnDatePickerDismiss : RecurringExpenseFormAction
    data class OnCategorySelected(val categoryId: Long) : RecurringExpenseFormAction
    data object OnCategoryDropdownToggle : RecurringExpenseFormAction
    data object OnCategoryDropdownDismiss : RecurringExpenseFormAction
    data class OnActiveToggle(val isActive: Boolean) : RecurringExpenseFormAction
    data object OnSaveClick : RecurringExpenseFormAction
    data object OnDeleteClick : RecurringExpenseFormAction
    data object OnDeleteConfirm : RecurringExpenseFormAction
    data object OnDeleteDismiss : RecurringExpenseFormAction
}
