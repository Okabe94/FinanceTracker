package com.software.financetracker.feature.metrics

sealed interface MetricsAction {
    data object OnBackClick : MetricsAction
    data class OnRangeSelected(val range: TrendRange) : MetricsAction
    data class OnCategorySelected(val categoryId: Long?) : MetricsAction
}
