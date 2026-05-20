package com.software.financetracker.feature.investment.list

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

data class InvestmentListState(
    val isLoading: Boolean = true,
    val investments: List<InvestmentCardUiModel> = emptyList()
)
