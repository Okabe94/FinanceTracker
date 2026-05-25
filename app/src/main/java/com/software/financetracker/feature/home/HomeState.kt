package com.software.financetracker.feature.home

import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.feature.goal.list.GoalUiModel
import com.software.financetracker.feature.investment.list.SortDirection

enum class HomeSortField {
    ALPHABETICAL, AMOUNT_SPENT, BUDGET_LIMIT, LAST_UPDATED
}

data class CategoryUiModel(
    val id: Long,
    val name: String,
    val colorArgb: Int,
    val iconKey: String,
    val amountSpent: Long,
    val monthlyLimit: Long?,
    val isOverLimit: Boolean,
    val updatedAt: Long = 0L
)

data class HomeState(
    val selectedMonth: String = DateUtil.currentYearMonth(),
    val displayMonth: String = DateUtil.formatMonth(DateUtil.currentYearMonth()),
    val isCurrentMonth: Boolean = true,
    val categories: List<CategoryUiModel> = emptyList(),
    val totalSpent: Long = 0L,
    val totalLimit: Long = 0L,
    val hasAnyLimit: Boolean = false,
    val isLoading: Boolean = true,
    val totalIncomeCop: Long = 0L,
    val netBalanceCop: Long = 0L,
    val hasIncomeData: Boolean = false,
    val activeGoals: List<GoalUiModel> = emptyList(),
    val hasGoals: Boolean = false,
    val sortField: HomeSortField = HomeSortField.ALPHABETICAL,
    val sortDirection: SortDirection = SortDirection.ASC,
    val showSortBottomSheet: Boolean = false
)
