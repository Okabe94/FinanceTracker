package com.software.financetracker.feature.recurring.form

import com.software.financetracker.core.presentation.UiText

sealed interface RecurringExpenseFormEvent {
    data object NavigateBack : RecurringExpenseFormEvent
    data class ShowError(val message: UiText) : RecurringExpenseFormEvent
}
