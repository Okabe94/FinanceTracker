package com.software.financetracker.feature.investment.list

sealed interface InvestmentListEvent {
    data object NavigateToAddForm : InvestmentListEvent
    data class NavigateToDetail(val investmentId: Long) : InvestmentListEvent
}
