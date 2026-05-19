package com.software.financetracker.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.software.financetracker.core.util.DateUtil
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

class HomeViewModel(
    private val categoryRepository: CategoryRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(DateUtil.currentYearMonth())
    private val _events = Channel<HomeEvent>()
    val events = _events.receiveAsFlow()

    val state = _selectedMonth.flatMapLatest { month ->
        combine(
            categoryRepository.observeAll(),
            expenseRepository.observeMonthlyTotalsByCategory(month)
        ) { categories, totals ->
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
                    isOverLimit = cat.monthlyLimitCop != null && spent > cat.monthlyLimitCop
                )
            }
            val limitedCats = uiModels.filter { it.monthlyLimit != null }
            HomeState(
                selectedMonth = month,
                displayMonth = DateUtil.formatMonth(month),
                isCurrentMonth = month == DateUtil.currentYearMonth(),
                categories = uiModels,
                totalSpent = uiModels.sumOf { it.amountSpent },
                totalLimit = limitedCats.sumOf { it.monthlyLimit ?: 0L },
                hasAnyLimit = limitedCats.isNotEmpty(),
                isLoading = false
            )
        }
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
                    _events.send(
                        HomeEvent.NavigateToCategoryDetail(action.categoryId, _selectedMonth.value)
                    )
                }
            HomeAction.OnAddCategoryClick ->
                viewModelScope.launch { _events.send(HomeEvent.NavigateToAddCategory) }
            HomeAction.OnMetricsClick ->
                viewModelScope.launch { _events.send(HomeEvent.NavigateToMetrics) }
            is HomeAction.OnAddExpenseClick ->
                viewModelScope.launch { _events.send(HomeEvent.NavigateToAddExpense(action.categoryId)) }
        }
    }
}
