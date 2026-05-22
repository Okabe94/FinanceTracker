package com.software.financetracker.feature.expense.assistant

import com.software.financetracker.core.presentation.UiText

sealed interface AssistantExpenseEvent {
    data object NavigateBack : AssistantExpenseEvent
    data class ShowError(val message: UiText) : AssistantExpenseEvent
}
