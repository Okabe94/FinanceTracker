package com.software.financetracker.feature.goal.list

sealed interface GoalListAction {
    data object OnBackClick : GoalListAction
    data object OnAddClick : GoalListAction
    data class OnGoalClick(val goalId: Long) : GoalListAction
}
