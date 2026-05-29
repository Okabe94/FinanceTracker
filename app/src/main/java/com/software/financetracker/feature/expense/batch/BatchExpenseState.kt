package com.software.financetracker.feature.expense.batch

import com.software.financetracker.feature.expense.form.CategoryItem

data class BatchExpenseState(
    val rows: List<ExpenseRowItem> = listOf(ExpenseRowItem(rowId = 0)),
    val categories: List<CategoryItem> = emptyList(),
    val openDropdownRowId: Int? = null,
    val isSaving: Boolean = false
)
