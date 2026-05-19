package com.software.financetracker.feature.metrics

sealed interface MetricsEvent {
    data object NavigateBack : MetricsEvent
}
