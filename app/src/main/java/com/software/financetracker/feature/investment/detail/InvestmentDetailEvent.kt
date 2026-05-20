package com.software.financetracker.feature.investment.detail

sealed interface InvestmentDetailEvent {
    data object NavigateBack : InvestmentDetailEvent
    data class NavigateToEditInvestment(val investmentId: Long) : InvestmentDetailEvent
    data class NavigateToAddEntry(val investmentId: Long) : InvestmentDetailEvent
    data class NavigateToEditEntry(val investmentId: Long, val entryId: Long) : InvestmentDetailEvent
}
