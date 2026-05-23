package com.software.financetracker.feature.investment.detail

import com.software.financetracker.data.local.investment.InvestmentEntryEntity

data class SnapshotPoint(val dateLabel: String, val amountMinorUnits: Long, val rawDate: String = "")

data class EntryUiModel(
    val id: Long,
    val typeLabel: String,
    val typeColor: Long,
    val amountFormatted: String?,
    val dateDisplay: String,
    val notes: String
)

data class InvestmentDetailState(
    val isLoading: Boolean = true,
    val investmentId: Long = 0L,
    val investmentName: String = "",
    val currency: String = "COP",
    val colorArgb: Int = 0,
    val iconKey: String = "",
    val annualRatePercent: Double? = null,
    val maturityDateDisplay: String? = null,
    val currentValueFormatted: String = "",
    val totalInvestedFormatted: String = "",
    val returnFormatted: String = "",
    val returnPercent: Float? = null,
    val isPositiveReturn: Boolean = true,
    val dividendsFormatted: String = "",
    val valueSnapshots: List<SnapshotPoint> = emptyList(),
    val entries: List<EntryUiModel> = emptyList(),
    val investmentEntries: List<InvestmentEntryEntity> = emptyList(),
    val showDeleteInvestmentDialog: Boolean = false,
    val benchmarkRatePercent: Double? = null,
    val showBenchmarkPicker: Boolean = false,
    val benchmarkChartData: List<Float> = emptyList(),
    val targetValueMinorUnits: Long? = null,
    val targetValueFormatted: String? = null,
    val targetDateDisplay: String? = null,
    val targetProgress: Float? = null
)
