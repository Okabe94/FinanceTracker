package com.software.financetracker.feature.investment.detail

sealed interface InvestmentDetailAction {
    data object OnBackClick : InvestmentDetailAction
    data object OnEditClick : InvestmentDetailAction
    data object OnAddEntryClick : InvestmentDetailAction
    data object OnBatchAddEntryClick : InvestmentDetailAction
    data class OnEntryClick(val entryId: Long) : InvestmentDetailAction
    data object OnDeleteInvestmentClick : InvestmentDetailAction
    data object OnDeleteInvestmentConfirm : InvestmentDetailAction
    data object OnDeleteInvestmentDismiss : InvestmentDetailAction
    data class DeleteEntrySwipe(val entryId: Long) : InvestmentDetailAction
    data object UndoDeleteEntry : InvestmentDetailAction
    data object SaveEntries : InvestmentDetailAction
    data object ShareEntries : InvestmentDetailAction
    data class OnBenchmarkRateChanged(val rate: Double?) : InvestmentDetailAction
    data object OnBenchmarkPickerToggled : InvestmentDetailAction
}
