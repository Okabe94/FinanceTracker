package com.software.financetracker.feature.investment.list

import com.software.financetracker.ui.components.DonutSlice

enum class SortField {
    AMOUNT_INVESTED, PERFORMANCE, ALPHABETICAL, NEWEST, LAST_UPDATED
}

enum class SortDirection { ASC, DESC }

data class InvestmentCardUiModel(
    val id: Long,
    val name: String,
    val currency: String,
    val colorArgb: Int,
    val iconKey: String,
    val currentValueFormatted: String,
    val returnPercent: Float?,
    val isPositiveReturn: Boolean
)

data class PortfolioSummary(
    val totalValueMinorUnits: Long,
    val totalInvestedMinorUnits: Long,
    val returnMinorUnits: Long,
    val returnPercent: Float?,
    val isCopOnly: Boolean
)

data class InvestmentListState(
    val isLoading: Boolean = true,
    val investments: List<InvestmentCardUiModel> = emptyList(),
    val portfolioSummary: PortfolioSummary? = null,
    val allocationSlices: List<DonutSlice> = emptyList(),
    val availableCurrencies: List<String> = emptyList(),
    val searchQuery: String = "",
    val activeCurrencyFilter: String? = null,
    val totalCount: Int = 0,
    val rates: Map<String, Double> = emptyMap(),
    val ratesUpdatedAt: String? = null,
    val isRefreshingRates: Boolean = false,
    val showRatesBottomSheet: Boolean = false,
    val sortField: SortField = SortField.ALPHABETICAL,
    val sortDirection: SortDirection = SortDirection.ASC,
    val showSortBottomSheet: Boolean = false
)
