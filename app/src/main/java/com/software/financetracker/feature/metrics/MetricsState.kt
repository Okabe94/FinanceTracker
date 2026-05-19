package com.software.financetracker.feature.metrics

import com.software.financetracker.core.util.DateUtil

data class MonthlyTotalUiModel(val yearMonth: String, val label: String, val totalCop: Long)

data class CategorySliceUiModel(val categoryId: Long, val name: String, val colorArgb: Int, val totalCop: Long)

data class CategoryTrendUiModel(val yearMonth: String, val label: String, val slices: List<CategorySliceUiModel>)

data class TopExpenseUiModel(
    val description: String,
    val amountCop: Long,
    val categoryName: String,
    val colorArgb: Int,
    val dateLabel: String
)

data class DayOfWeekUiModel(val dayLabel: String, val totalCop: Long)

data class OverLimitCategoryUiModel(
    val categoryName: String,
    val colorArgb: Int,
    val spentCop: Long,
    val limitCop: Long
)

data class OverLimitMonthUiModel(val monthLabel: String, val categories: List<OverLimitCategoryUiModel>)

data class MetricsState(
    val currentMonthLabel: String = DateUtil.formatMonth(DateUtil.currentYearMonth()),
    val currentMonthTotalCop: Long = 0L,
    val currentMonthLimitCop: Long = 0L,
    val currentMonthHasLimit: Boolean = false,
    val currentMonthOverLimitCount: Int = 0,

    val selectedRange: TrendRange = TrendRange.SIX_MONTHS,

    val monthlyTotals: List<MonthlyTotalUiModel> = emptyList(),

    val categoryTrend: List<CategoryTrendUiModel> = emptyList(),
    val allCategories: List<CategorySliceUiModel> = emptyList(),

    val momDeltaPercent: Float? = null,
    val momDeltaLabel: String = "",

    val averageMonthlySpend: Long = 0L,

    val topExpenses: List<TopExpenseUiModel> = emptyList(),

    val topCategoryName: String = "",
    val topCategoryColorArgb: Int = 0,
    val topCategoryTotalCop: Long = 0L,
    val topCategorySharePercent: Int = 0,

    val daysLeftInMonth: Int = 0,
    val dailyAvgCop: Long = 0L,
    val projectedMonthCop: Long = 0L,

    val bestMonthLabel: String = "",
    val bestMonthTotalCop: Long = 0L,
    val worstMonthLabel: String = "",
    val worstMonthTotalCop: Long = 0L,

    val spendByDayOfWeek: List<DayOfWeekUiModel> = emptyList(),

    val selectedCategoryId: Long? = null,

    val overLimitByMonth: List<OverLimitMonthUiModel> = emptyList(),

    val isLoading: Boolean = true
)
