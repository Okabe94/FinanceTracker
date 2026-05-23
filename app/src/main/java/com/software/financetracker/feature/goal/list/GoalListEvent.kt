package com.software.financetracker.feature.goal.list

sealed interface GoalListEvent {
    data object NavigateBack : GoalListEvent
    data object NavigateToAddForm : GoalListEvent
    data class NavigateToDetail(val goalId: Long) : GoalListEvent
}
