package com.software.financetracker.feature.investment.form

import com.software.financetracker.core.presentation.UiText

sealed interface InvestmentFormEvent {
    data object NavigateBack : InvestmentFormEvent
    data class ShowError(val message: UiText) : InvestmentFormEvent
}
