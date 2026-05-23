package com.software.financetracker.feature.goal.detail

sealed interface GoalDetailEvent {
    data object NavigateBack : GoalDetailEvent
    data class NavigateToEdit(val goalId: Long) : GoalDetailEvent
}
