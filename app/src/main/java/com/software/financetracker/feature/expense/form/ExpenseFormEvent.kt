package com.software.financetracker.feature.expense.form

import com.software.financetracker.core.presentation.UiText

sealed interface ExpenseFormEvent {
    data object NavigateBack : ExpenseFormEvent
    data class ShowError(val message: UiText) : ExpenseFormEvent
}
