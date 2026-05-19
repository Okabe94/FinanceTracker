package com.software.financetracker.feature.category.detail

sealed interface CategoryDetailEvent {
    data object NavigateBack : CategoryDetailEvent
    data class NavigateToEditCategory(val categoryId: Long) : CategoryDetailEvent
    data class NavigateToAddExpense(val categoryId: Long) : CategoryDetailEvent
    data class NavigateToEditExpense(val categoryId: Long, val expenseId: Long) : CategoryDetailEvent
}
