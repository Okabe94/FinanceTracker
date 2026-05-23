package com.software.financetracker.feature.goal.list

data class GoalListState(
    val activeGoals: List<GoalUiModel> = emptyList(),
    val achievedGoals: List<GoalUiModel> = emptyList(),
    val isLoading: Boolean = true
)
