package com.software.financetracker.feature.home

sealed interface HomeEvent {
    data class NavigateToCategoryDetail(val categoryId: Long, val selectedMonth: String) : HomeEvent
    data object NavigateToAddCategory : HomeEvent
    data object NavigateToMetrics : HomeEvent
    data object NavigateToAddExpense : HomeEvent
    data object NavigateToAddIncome : HomeEvent
    data object NavigateToAddGoal : HomeEvent
    data object NavigateToIncomeList : HomeEvent
    data class NavigateToGoalDetail(val goalId: Long) : HomeEvent
    data object NavigateToGoalList : HomeEvent
}
