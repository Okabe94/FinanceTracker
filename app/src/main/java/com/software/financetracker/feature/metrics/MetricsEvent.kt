package com.software.financetracker.feature.metrics

sealed interface MetricsEvent {
    data object NavigateBack : MetricsEvent
    data class SaveReady(val csvContent: String) : MetricsEvent
    data class ShareReady(val csvContent: String) : MetricsEvent
}
