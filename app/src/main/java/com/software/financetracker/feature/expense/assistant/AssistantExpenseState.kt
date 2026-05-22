package com.software.financetracker.feature.expense.assistant

import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.data.local.category.CategoryEntity

data class AssistantExpenseState(
    val isLoading: Boolean = true,
    val categories: List<CategoryEntity> = emptyList(),
    val selectedCategory: CategoryEntity? = null,
    val amountInput: String = "",
    val amountError: UiText? = null,
    val categoryError: UiText? = null,
    val isSaving: Boolean = false
)
