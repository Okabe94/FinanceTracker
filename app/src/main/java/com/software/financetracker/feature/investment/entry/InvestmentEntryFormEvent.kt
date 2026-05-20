package com.software.financetracker.feature.investment.entry

import com.software.financetracker.core.presentation.UiText

sealed interface InvestmentEntryFormEvent {
    data object NavigateBack : InvestmentEntryFormEvent
    data class ShowError(val message: UiText) : InvestmentEntryFormEvent
}
