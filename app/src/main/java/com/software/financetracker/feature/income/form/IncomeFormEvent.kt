package com.software.financetracker.feature.income.form

import com.software.financetracker.core.presentation.UiText

sealed interface IncomeFormEvent {
    data object NavigateBack : IncomeFormEvent
    data class ShowError(val message: UiText) : IncomeFormEvent
}
