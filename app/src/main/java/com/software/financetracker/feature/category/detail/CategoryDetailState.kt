package com.software.financetracker.feature.category.detail

import com.software.financetracker.core.util.DateUtil

data class CategoryDetailState(
    val categoryId: Long = 0L,
    val categoryName: String = "",
    val categoryColorArgb: Int = 0xFF33B679.toInt(),
    val categoryIconKey: String = "more_horiz",
    val monthlyLimitCop: Long? = null,
    val selectedMonth: String = DateUtil.currentYearMonth(),
    val displayMonth: String = "",
    val isCurrentMonth: Boolean = true,
    val amountSpent: Long = 0L,
    val expenses: List<ExpenseUiModel> = emptyList(),
    val recurringExpenses: List<RecurringTemplateUi> = emptyList(),
    val isLoading: Boolean = true,
    val isOverLimit: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false
)
