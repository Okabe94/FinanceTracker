package com.software.financetracker.feature.investment.entry.batch

import com.software.financetracker.core.presentation.UiText

sealed interface BatchInvestmentEntryEvent {
    data object NavigateBack : BatchInvestmentEntryEvent
    data class ShowError(val message: UiText) : BatchInvestmentEntryEvent
}
