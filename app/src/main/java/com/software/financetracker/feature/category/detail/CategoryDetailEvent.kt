package com.software.financetracker.feature.category.detail

sealed interface CategoryDetailEvent {
    data object NavigateBack : CategoryDetailEvent
    data class NavigateToEditCategory(val categoryId: Long) : CategoryDetailEvent
    data class NavigateToExpenseForm(
        val categoryId: Long,
        val expenseId: Long? = null,
        val recurringExpenseId: Long? = null
    ) : CategoryDetailEvent
    data class NavigateToBatchExpense(val categoryId: Long) : CategoryDetailEvent
}
