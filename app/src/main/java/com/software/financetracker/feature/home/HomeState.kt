package com.software.financetracker.feature.home

import com.software.financetracker.core.util.DateUtil

data class CategoryUiModel(
    val id: Long,
    val name: String,
    val colorArgb: Int,
    val iconKey: String,
    val amountSpent: Long,
    val monthlyLimit: Long?,
    val isOverLimit: Boolean
)

data class HomeState(
    val selectedMonth: String = DateUtil.currentYearMonth(),
    val displayMonth: String = DateUtil.formatMonth(DateUtil.currentYearMonth()),
    val isCurrentMonth: Boolean = true,
    val categories: List<CategoryUiModel> = emptyList(),
    val totalSpent: Long = 0L,
    val totalLimit: Long = 0L,
    val hasAnyLimit: Boolean = false,
    val isLoading: Boolean = true
)
