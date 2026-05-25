package com.software.financetracker.feature.investment.list

sealed interface InvestmentListAction {
    data object OnAddClick : InvestmentListAction
    data class OnCardClick(val investmentId: Long) : InvestmentListAction
    data class OnSearchQueryChanged(val query: String) : InvestmentListAction
    data class OnCurrencyFilterChanged(val currency: String?) : InvestmentListAction
    data object RefreshRates : InvestmentListAction
    data object OnRatesBottomSheetToggled : InvestmentListAction
    data object OnSortBottomSheetToggled : InvestmentListAction
    data class OnSortChanged(val field: SortField, val direction: SortDirection) : InvestmentListAction
}
