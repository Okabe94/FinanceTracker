package com.software.financetracker.feature.investment.detail

sealed interface InvestmentDetailAction {
    data object OnBackClick : InvestmentDetailAction
    data object OnEditClick : InvestmentDetailAction
    data object OnAddEntryClick : InvestmentDetailAction
    data class OnEntryClick(val entryId: Long) : InvestmentDetailAction
    data object OnDeleteInvestmentClick : InvestmentDetailAction
    data object OnDeleteInvestmentConfirm : InvestmentDetailAction
    data object OnDeleteInvestmentDismiss : InvestmentDetailAction
}
