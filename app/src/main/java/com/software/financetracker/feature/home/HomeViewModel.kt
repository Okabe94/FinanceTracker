package com.software.financetracker.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.software.financetracker.core.preferences.UserPreferences
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.data.local.goal.GoalEntity
import com.software.financetracker.domain.repository.CategoryRepository
import com.software.financetracker.domain.repository.ExpenseRepository
import com.software.financetracker.domain.repository.GoalRepository
import com.software.financetracker.domain.repository.IncomeRepository
import com.software.financetracker.feature.goal.list.GoalUiModel
import com.software.financetracker.feature.investment.list.SortDirection
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val categoryRepository: CategoryRepository,
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository,
    private val goalRepository: GoalRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(DateUtil.currentYearMonth())
    private val _showSortBottomSheet = MutableStateFlow(false)
    private val _events = Channel<HomeEvent>()
    val events = _events.receiveAsFlow()

    private val _sortOptions = combine(
        prefs.homeSortField.map { runCatching { HomeSortField.valueOf(it) }.getOrDefault(HomeSortField.ALPHABETICAL) },
        prefs.homeSortDirection.map { runCatching { SortDirection.valueOf(it) }.getOrDefault(SortDirection.ASC) }
    ) { field, dir -> field to dir }

    val state = combine(
        _selectedMonth.flatMapLatest { month ->
            val monthStart = "$month-01"
            val yearMonthObj = YearMonth.parse(month)
            val monthEnd = "$month-${yearMonthObj.lengthOfMonth().toString().padStart(2, '0')}"
            combine(
                categoryRepository.observeAll(),
                expenseRepository.observeMonthlyTotalsByCategory(month),
                incomeRepository.observeTotalInRange(monthStart, monthEnd)
            ) { categories, totals, incomeTotal ->
                val totalsMap = totals.associate { it.categoryId to it.total }
                val uiModels = categories.map { cat ->
                    val spent = totalsMap[cat.id] ?: 0L
                    CategoryUiModel(
                        id = cat.id,
                        name = cat.name,
                        colorArgb = cat.colorArgb,
                        iconKey = cat.iconKey,
                        amountSpent = spent,
                        monthlyLimit = cat.monthlyLimitCop,
                        isOverLimit = cat.monthlyLimitCop != null && spent > cat.monthlyLimitCop,
                        updatedAt = cat.updatedAt
                    )
                }
                val limitedCats = uiModels.filter { it.monthlyLimit != null }
                val totalSpent = uiModels.sumOf { it.amountSpent }
                val totalIncome = incomeTotal ?: 0L
                Triple(
                    month,
                    uiModels,
                    HomePartialState(
                        totalSpent = totalSpent,
                        totalLimit = limitedCats.sumOf { it.monthlyLimit ?: 0L },
                        hasAnyLimit = limitedCats.isNotEmpty(),
                        totalIncomeCop = totalIncome,
                        netBalanceCop = totalIncome - totalSpent,
                        hasIncomeData = totalIncome > 0L
                    )
                )
            }
        },
        goalRepository.observeActive(),
        _sortOptions
    ) { (month, uiModels, partial), activeGoals, sortOptions ->
        val (sortField, sortDirection) = sortOptions
        val goalUiModels = activeGoals.map { it.toGoalUiModel() }
        HomeState(
            selectedMonth = month,
            displayMonth = DateUtil.formatMonth(month),
            isCurrentMonth = month == DateUtil.currentYearMonth(),
            categories = uiModels.applySorting(sortField, sortDirection),
            totalSpent = partial.totalSpent,
            totalLimit = partial.totalLimit,
            hasAnyLimit = partial.hasAnyLimit,
            isLoading = false,
            totalIncomeCop = partial.totalIncomeCop,
            netBalanceCop = partial.netBalanceCop,
            hasIncomeData = partial.hasIncomeData,
            activeGoals = goalUiModels,
            hasGoals = goalUiModels.isNotEmpty(),
            sortField = sortField,
            sortDirection = sortDirection
        )
    }.combine(_showSortBottomSheet) { s, show ->
        s.copy(showSortBottomSheet = show)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), HomeState())

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnPreviousMonthClick ->
                _selectedMonth.update { DateUtil.previousMonth(it) }
            HomeAction.OnNextMonthClick ->
                _selectedMonth.update { DateUtil.nextMonth(it) }
            HomeAction.OnGoToCurrentMonthClick ->
                _selectedMonth.update { DateUtil.currentYearMonth() }
            is HomeAction.OnCategoryClick ->
                viewModelScope.launch {
                    _events.send(HomeEvent.NavigateToCategoryDetail(action.categoryId, _selectedMonth.value))
                }
            HomeAction.OnAddCategoryClick ->
                viewModelScope.launch { _events.send(HomeEvent.NavigateToAddCategory) }
            HomeAction.OnMetricsClick ->
                viewModelScope.launch { _events.send(HomeEvent.NavigateToMetrics) }
            HomeAction.OnAddExpenseClick ->
                viewModelScope.launch { _events.send(HomeEvent.NavigateToAddExpense) }
            HomeAction.OnAddIncomeClick ->
                viewModelScope.launch { _events.send(HomeEvent.NavigateToAddIncome) }
            HomeAction.OnAddGoalClick ->
                viewModelScope.launch { _events.send(HomeEvent.NavigateToAddGoal) }
            HomeAction.OnIncomeCardClick ->
                viewModelScope.launch { _events.send(HomeEvent.NavigateToIncomeList) }
            is HomeAction.OnGoalCardClick ->
                viewModelScope.launch { _events.send(HomeEvent.NavigateToGoalDetail(action.goalId)) }
            HomeAction.OnViewAllGoalsClick ->
                viewModelScope.launch { _events.send(HomeEvent.NavigateToGoalList) }
            HomeAction.OnSortBottomSheetToggled ->
                _showSortBottomSheet.update { !it }
            is HomeAction.OnSortChanged ->
                viewModelScope.launch {
                    prefs.setHomeSort(action.field.name, action.direction.name)
                }
        }
    }
}

private fun List<CategoryUiModel>.applySorting(
    field: HomeSortField,
    direction: SortDirection
): List<CategoryUiModel> = when (field) {
    HomeSortField.ALPHABETICAL -> {
        val sorted = sortedBy { it.name.lowercase() }
        if (direction == SortDirection.DESC) sorted.reversed() else sorted
    }
    HomeSortField.AMOUNT_SPENT -> {
        val sorted = sortedBy { it.amountSpent }
        if (direction == SortDirection.DESC) sorted.reversed() else sorted
    }
    HomeSortField.BUDGET_LIMIT -> {
        val (withLimit, withoutLimit) = partition { it.monthlyLimit != null }
        val sorted = withLimit.sortedBy { it.monthlyLimit }
        val directed = if (direction == SortDirection.DESC) sorted.reversed() else sorted
        directed + withoutLimit
    }
    HomeSortField.LAST_UPDATED -> {
        val sorted = sortedBy { it.updatedAt }
        if (direction == SortDirection.DESC) sorted.reversed() else sorted
    }
}

private data class HomePartialState(
    val totalSpent: Long,
    val totalLimit: Long,
    val hasAnyLimit: Boolean,
    val totalIncomeCop: Long,
    val netBalanceCop: Long,
    val hasIncomeData: Boolean
)

private fun GoalEntity.toGoalUiModel(): GoalUiModel {
    val today = LocalDate.now()
    val deadline = LocalDate.parse(deadlineDate)
    val monthsLeft = ChronoUnit.MONTHS.between(YearMonth.now(), YearMonth.from(deadline))
    val remaining = (targetAmountCop - currentAmountCop).coerceAtLeast(0L)
    val progress = if (targetAmountCop > 0L) (currentAmountCop.toFloat() / targetAmountCop * 100f).coerceIn(0f, 100f) else 0f
    return GoalUiModel(
        id = id,
        name = name,
        targetAmountCop = targetAmountCop,
        currentAmountCop = currentAmountCop,
        progressPercent = progress,
        remainingCop = remaining,
        deadlineDisplay = DateUtil.toDisplayDate(deadlineDate),
        requiredMonthlyCop = if (monthsLeft > 0L && !isAchieved) remaining / monthsLeft else null,
        isOverdue = !isAchieved && deadline.isBefore(today),
        colorArgb = colorArgb
    )
}
