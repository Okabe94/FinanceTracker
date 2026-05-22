package com.software.financetracker.feature.expense.assistant

import com.software.financetracker.data.local.category.CategoryEntity

sealed interface AssistantExpenseAction {
    data object OnBackClick : AssistantExpenseAction
    data class OnCategorySelected(val category: CategoryEntity) : AssistantExpenseAction
    data class OnAmountChange(val value: String) : AssistantExpenseAction
    data object OnSaveClick : AssistantExpenseAction
}
