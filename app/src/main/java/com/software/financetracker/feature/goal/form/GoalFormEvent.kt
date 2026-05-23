package com.software.financetracker.feature.goal.form

import com.software.financetracker.core.presentation.UiText

sealed interface GoalFormEvent {
    data object NavigateBack : GoalFormEvent
    data class ShowError(val message: UiText) : GoalFormEvent
}
