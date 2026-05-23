package com.software.financetracker.feature.income.recurring.form

import com.software.financetracker.core.presentation.UiText

sealed interface RecurringIncomeFormEvent {
    data object NavigateBack : RecurringIncomeFormEvent
    data class ShowError(val message: UiText) : RecurringIncomeFormEvent
}
