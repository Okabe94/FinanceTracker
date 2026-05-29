package com.software.financetracker.feature.investment.entry.batch

data class BatchInvestmentEntryState(
    val investmentId: Long = 0L,
    val investmentCurrency: String = "COP",
    val rows: List<InvestmentEntryRowItem> = listOf(InvestmentEntryRowItem(rowId = 0)),
    val isSaving: Boolean = false
)
