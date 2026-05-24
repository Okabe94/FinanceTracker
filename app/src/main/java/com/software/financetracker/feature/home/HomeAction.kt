package com.software.financetracker.feature.home

sealed interface HomeAction {
    data object OnPreviousMonthClick : HomeAction
    data object OnNextMonthClick : HomeAction
    data object OnGoToCurrentMonthClick : HomeAction
    data class OnCategoryClick(val categoryId: Long) : HomeAction
    data object OnAddCategoryClick : HomeAction
    data object OnMetricsClick : HomeAction
    data object OnAddExpenseClick : HomeAction
    data object OnAddIncomeClick : HomeAction
    data object OnAddGoalClick : HomeAction
    data object OnIncomeCardClick : HomeAction
    data class OnGoalCardClick(val goalId: Long) : HomeAction
    data object OnViewAllGoalsClick : HomeAction
}
