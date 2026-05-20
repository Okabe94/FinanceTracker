package com.software.financetracker.domain.model.investment

data class InvestmentMetrics(
    val totalInvestedMinorUnits: Long,
    val currentValueMinorUnits: Long,
    val returnMinorUnits: Long,
    val returnPercent: Float?,
    val dividendsTotalMinorUnits: Long
)
