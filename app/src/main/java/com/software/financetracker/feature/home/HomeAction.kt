package com.software.financetracker.feature.home

sealed interface HomeAction {
    data object OnPreviousMonthClick : HomeAction
    data object OnNextMonthClick : HomeAction
    data object OnGoToCurrentMonthClick : HomeAction
    data class OnCategoryClick(val categoryId: Long) : HomeAction
    data object OnAddCategoryClick : HomeAction
    data object OnMetricsClick : HomeAction
    data object OnRecurringExpensesClick : HomeAction
    data object OnAddIncomeClick : HomeAction
    data object OnViewIncomeClick : HomeAction
    data object OnGoalsClick : HomeAction
}
