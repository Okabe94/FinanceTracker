package com.software.financetracker.feature.category.detail

sealed interface CategoryDetailAction {
    data object OnBackClick : CategoryDetailAction
    data object OnEditCategoryClick : CategoryDetailAction
    data object OnAddExpenseClick : CategoryDetailAction
    data object OnBatchAddExpenseClick : CategoryDetailAction
    data class OnExpenseClick(val expenseId: Long) : CategoryDetailAction
    data class OnRecurringExpenseClick(val recurringExpenseId: Long) : CategoryDetailAction
    data object OnDeleteCategoryClick : CategoryDetailAction
    data object OnDeleteConfirm : CategoryDetailAction
    data object OnDeleteDismiss : CategoryDetailAction
    data object OnPreviousMonthClick : CategoryDetailAction
    data object OnNextMonthClick : CategoryDetailAction
    data object OnGoToCurrentMonthClick : CategoryDetailAction
}
