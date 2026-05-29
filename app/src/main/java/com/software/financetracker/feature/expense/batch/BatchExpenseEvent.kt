package com.software.financetracker.feature.expense.batch

import com.software.financetracker.core.presentation.UiText

sealed interface BatchExpenseEvent {
    data object NavigateBack : BatchExpenseEvent
    data class ShowError(val message: UiText) : BatchExpenseEvent
}
