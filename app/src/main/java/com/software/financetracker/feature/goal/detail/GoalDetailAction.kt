package com.software.financetracker.feature.goal.detail

sealed interface GoalDetailAction {
    data object OnBackClick : GoalDetailAction
    data object OnEditClick : GoalDetailAction
    data object OnAddContributionClick : GoalDetailAction
    data class OnContributionChange(val value: String) : GoalDetailAction
    data object OnContributionConfirm : GoalDetailAction
    data object OnContributionDismiss : GoalDetailAction
    data object OnMarkAchievedClick : GoalDetailAction
}
