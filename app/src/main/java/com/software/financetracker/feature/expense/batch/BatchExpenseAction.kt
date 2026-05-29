package com.software.financetracker.feature.expense.batch

sealed interface BatchExpenseAction {
    data object OnAddRow : BatchExpenseAction
    data class OnRemoveRow(val rowId: Int) : BatchExpenseAction
    data class OnAmountChange(val rowId: Int, val value: String) : BatchExpenseAction
    data class OnCategorySelect(val rowId: Int, val categoryId: Long) : BatchExpenseAction
    data class OnToggleCategoryDropdown(val rowId: Int) : BatchExpenseAction
    data object OnDismissCategoryDropdown : BatchExpenseAction
    data class OnDescriptionChange(val rowId: Int, val value: String) : BatchExpenseAction
    data class OnDateFieldClick(val rowId: Int) : BatchExpenseAction
    data class OnDateSelected(val rowId: Int, val epochMillis: Long) : BatchExpenseAction
    data object OnDismissDatePicker : BatchExpenseAction
    data object OnSaveAll : BatchExpenseAction
    data object OnBackClick : BatchExpenseAction
}
