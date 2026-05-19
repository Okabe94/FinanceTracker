package com.software.financetracker.feature.metrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.data.local.category.CategoryEntity
import com.software.financetracker.data.local.expense.CategoryMonthTotal
import com.software.financetracker.data.local.expense.CategoryMonthlyBreakdown
import com.software.financetracker.data.local.expense.DayOfWeekTotal
import com.software.financetracker.data.local.expense.MonthlyTotal
import com.software.financetracker.data.local.expense.TopExpenseRow
import com.software.financetracker.domain.repository.CategoryRepository
import com.software.financetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class MetricsViewModel(
    private val categoryRepository: CategoryRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val currentMonth = DateUtil.currentYearMonth()
    private val currentYm = YearMonth.now()

    private val _selectedRange = MutableStateFlow(TrendRange.SIX_MONTHS)
    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    private val _events = Channel<MetricsEvent>()
    val events = _events.receiveAsFlow()

    val state = _selectedRange.flatMapLatest { range ->
        val (startDate, endDate) = dateRangeFor(range)

        val catsAndCurrentFlow = combine(
            categoryRepository.observeAll(),
            expenseRepository.observeMonthlyTotalsByCategory(currentMonth)
        ) { cats, curTotals -> cats to curTotals }

        val rangeDataFlow = combine(
            expenseRepository.observeMonthlyTotals(startDate, endDate),
            expenseRepository.observeCategoryMonthlyBreakdown(startDate, endDate)
        ) { totals, breakdown -> totals to breakdown }

        val extraFlow = combine(
            expenseRepository.observeTopExpenses(currentMonth, 5),
            expenseRepository.observeSpendByDayOfWeek(startDate, endDate)
        ) { top, dow -> top to dow }

        combine(catsAndCurrentFlow, rangeDataFlow, extraFlow, _selectedCategoryId) { p1, p2, p3, selectedCatId ->
            val (cats, curTotals) = p1
            val (monthlyTotals, breakdown) = p2
            val (topRows, dowTotals) = p3
            buildState(range, selectedCatId, cats, curTotals, monthlyTotals, breakdown, topRows, dowTotals)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), MetricsState())

    fun onAction(action: MetricsAction) {
        when (action) {
            MetricsAction.OnBackClick ->
                viewModelScope.launch { _events.send(MetricsEvent.NavigateBack) }
            is MetricsAction.OnRangeSelected ->
                _selectedRange.update { action.range }
            is MetricsAction.OnCategorySelected ->
                _selectedCategoryId.update { action.categoryId }
        }
    }

    private fun buildState(
        range: TrendRange,
        selectedCatId: Long?,
        categories: List<CategoryEntity>,
        currentTotals: List<CategoryMonthTotal>,
        monthlyTotals: List<MonthlyTotal>,
        breakdown: List<CategoryMonthlyBreakdown>,
        topRows: List<TopExpenseRow>,
        dowTotals: List<DayOfWeekTotal>
    ): MetricsState {
        val currentTotalsMap = currentTotals.associate { it.categoryId to it.total }
        val currentSpent = categories.sumOf { currentTotalsMap[it.id] ?: 0L }
        val currentLimit = categories.sumOf { it.monthlyLimitCop ?: 0L }
        val hasLimit = categories.any { it.monthlyLimitCop != null }
        val overLimitCount = categories.count { cat ->
            cat.monthlyLimitCop != null && (currentTotalsMap[cat.id] ?: 0L) > cat.monthlyLimitCop!!
        }

        val monthlyUiModels = monthlyTotals.map { mt ->
            MonthlyTotalUiModel(yearMonth = mt.yearMonth, label = DateUtil.shortMonthLabel(mt.yearMonth), totalCop = mt.total)
        }

        val catMap = categories.associateBy { it.id }
        val breakdownByMonth = breakdown.groupBy { it.yearMonth }
        val categoryTrend = breakdownByMonth.entries.sortedBy { it.key }.map { (ym, slices) ->
            CategoryTrendUiModel(
                yearMonth = ym,
                label = DateUtil.shortMonthLabel(ym),
                slices = slices.mapNotNull { s ->
                    val cat = catMap[s.categoryId] ?: return@mapNotNull null
                    CategorySliceUiModel(categoryId = cat.id, name = cat.name, colorArgb = cat.colorArgb, totalCop = s.total)
                }
            )
        }

        val sortedTotals = monthlyTotals.sortedBy { it.yearMonth }
        val last = sortedTotals.lastOrNull()
        val secondLast = if (sortedTotals.size >= 2) sortedTotals[sortedTotals.size - 2] else null
        val momDelta = if (last != null && secondLast != null && secondLast.total > 0L)
            (last.total - secondLast.total).toFloat() / secondLast.total.toFloat() * 100f
        else null

        val avgSpend = if (monthlyTotals.isNotEmpty()) monthlyTotals.sumOf { it.total } / monthlyTotals.size else 0L

        val allCats = categories.map { cat ->
            CategorySliceUiModel(categoryId = cat.id, name = cat.name, colorArgb = cat.colorArgb, totalCop = 0L)
        }

        // Feature 1: Top expenses this month
        val topExpenses = topRows.map { row ->
            TopExpenseUiModel(
                description = row.description,
                amountCop = row.amountCop,
                categoryName = row.categoryName,
                colorArgb = row.colorArgb,
                dateLabel = DateUtil.toDisplayDate(row.date)
            )
        }

        // Feature 2: Category with highest spend this month
        val topCatEntry = currentTotalsMap.entries.maxByOrNull { it.value }
        val topCat = topCatEntry?.let { catMap[it.key] }
        val topCatShare = if (currentSpent > 0L && topCatEntry != null)
            ((topCatEntry.value.toFloat() / currentSpent) * 100).toInt()
        else 0

        // Feature 3: Burn rate
        val today = LocalDate.now()
        val dayOfMonth = today.dayOfMonth
        val daysLeft = currentYm.lengthOfMonth() - dayOfMonth
        val dailyAvg = if (dayOfMonth > 0) currentSpent / dayOfMonth else 0L

        // Feature 7: Month projection at current daily pace
        val projectedMonthCop = dailyAvg * currentYm.lengthOfMonth()

        // Feature 8: Best and worst month in the selected range (requires ≥ 2 months)
        val bestMonth = if (monthlyTotals.size >= 2) monthlyTotals.minByOrNull { it.total } else null
        val worstMonth = if (monthlyTotals.size >= 2) monthlyTotals.maxByOrNull { it.total } else null

        // Feature 4: Spend by day of week — reorder from Sun-Sat to Mon-Sun
        val dayLabels = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
        val dowMap = dowTotals.associate { it.dayOfWeek to it.total }
        val spendByDow = ((1..6).toList() + 0).map { day ->
            DayOfWeekUiModel(dayLabel = dayLabels[day], totalCop = dowMap[day] ?: 0L)
        }

        // Feature 6: Categories over limit per month in selected range
        val overLimitByMonth = breakdownByMonth.entries.sortedBy { it.key }.mapNotNull { (ym, slices) ->
            val overCats = slices.mapNotNull { s ->
                val cat = catMap[s.categoryId] ?: return@mapNotNull null
                val limit = cat.monthlyLimitCop ?: return@mapNotNull null
                if (s.total > limit) OverLimitCategoryUiModel(
                    categoryName = cat.name,
                    colorArgb = cat.colorArgb,
                    spentCop = s.total,
                    limitCop = limit
                ) else null
            }
            if (overCats.isEmpty()) null
            else OverLimitMonthUiModel(monthLabel = DateUtil.formatMonth(ym), categories = overCats)
        }

        return MetricsState(
            currentMonthLabel = DateUtil.formatMonth(currentMonth),
            currentMonthTotalCop = currentSpent,
            currentMonthLimitCop = currentLimit,
            currentMonthHasLimit = hasLimit,
            currentMonthOverLimitCount = overLimitCount,
            selectedRange = range,
            monthlyTotals = monthlyUiModels,
            categoryTrend = categoryTrend,
            allCategories = allCats,
            momDeltaPercent = momDelta,
            momDeltaLabel = momDelta?.let { formatDeltaLabel(it) } ?: "",
            averageMonthlySpend = avgSpend,
            topExpenses = topExpenses,
            topCategoryName = topCat?.name ?: "",
            topCategoryColorArgb = topCat?.colorArgb ?: 0,
            topCategoryTotalCop = topCatEntry?.value ?: 0L,
            topCategorySharePercent = topCatShare,
            daysLeftInMonth = daysLeft,
            dailyAvgCop = dailyAvg,
            projectedMonthCop = projectedMonthCop,
            bestMonthLabel = bestMonth?.let { DateUtil.formatMonth(it.yearMonth) } ?: "",
            bestMonthTotalCop = bestMonth?.total ?: 0L,
            worstMonthLabel = worstMonth?.let { DateUtil.formatMonth(it.yearMonth) } ?: "",
            worstMonthTotalCop = worstMonth?.total ?: 0L,
            spendByDayOfWeek = spendByDow,
            selectedCategoryId = selectedCatId,
            overLimitByMonth = overLimitByMonth,
            isLoading = false
        )
    }

    private fun dateRangeFor(range: TrendRange): Pair<String, String> =
        if (range == TrendRange.YTD) {
            val start = YearMonth.of(currentYm.year, 1).atDay(1).toString()
            val end = currentYm.atEndOfMonth().toString()
            start to end
        } else {
            DateUtil.rangeForMonthsBack(range.monthsBack)
        }

    private fun formatDeltaLabel(delta: Float): String {
        val sign = if (delta >= 0f) "+" else ""
        val formatted = String.format("%.1f", delta).replace('.', ',')
        return "${sign}${formatted}% vs. mes anterior"
    }
}
