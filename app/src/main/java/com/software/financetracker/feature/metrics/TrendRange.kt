package com.software.financetracker.feature.metrics

enum class TrendRange(val labelEs: String, val monthsBack: Int) {
    THREE_MONTHS("3M", 2),
    SIX_MONTHS("6M", 5),
    TWELVE_MONTHS("12M", 11),
    YTD("Este año", -1)
}
