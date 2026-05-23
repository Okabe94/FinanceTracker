package com.software.financetracker.feature.goal.detail

import com.software.financetracker.feature.goal.list.GoalUiModel

data class GoalDetailState(
    val goal: GoalUiModel? = null,
    val contributionInput: String = "",
    val showContributionDialog: Boolean = false,
    val isLoading: Boolean = true
)
