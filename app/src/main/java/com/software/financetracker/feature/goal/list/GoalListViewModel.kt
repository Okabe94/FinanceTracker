package com.software.financetracker.feature.goal.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.domain.repository.GoalRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

class GoalListViewModel(private val goalRepository: GoalRepository) : ViewModel() {

    private val _events = Channel<GoalListEvent>()
    val events = _events.receiveAsFlow()

    val state = combine(
        goalRepository.observeActive(),
        goalRepository.observeAchieved()
    ) { active, achieved ->
        GoalListState(
            activeGoals = active.map { it.toUiModel() },
            achievedGoals = achieved.map { it.toUiModel() },
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), GoalListState())

    fun onAction(action: GoalListAction) {
        when (action) {
            GoalListAction.OnBackClick ->
                viewModelScope.launch { _events.send(GoalListEvent.NavigateBack) }
            GoalListAction.OnAddClick ->
                viewModelScope.launch { _events.send(GoalListEvent.NavigateToAddForm) }
            is GoalListAction.OnGoalClick ->
                viewModelScope.launch { _events.send(GoalListEvent.NavigateToDetail(action.goalId)) }
        }
    }
}

internal fun com.software.financetracker.data.local.goal.GoalEntity.toUiModel(): GoalUiModel {
    val today = LocalDate.now()
    val deadline = LocalDate.parse(deadlineDate)
    val monthsLeft = ChronoUnit.MONTHS.between(YearMonth.now(), YearMonth.from(deadline))
    val remaining = (targetAmountCop - currentAmountCop).coerceAtLeast(0L)
    val progress = if (targetAmountCop > 0L) (currentAmountCop.toFloat() / targetAmountCop * 100f).coerceIn(0f, 100f) else 0f
    val isOverdue = !isAchieved && deadline.isBefore(today)
    val requiredMonthly = if (monthsLeft > 0L && !isAchieved) remaining / monthsLeft else null
    return GoalUiModel(
        id = id,
        name = name,
        targetAmountCop = targetAmountCop,
        currentAmountCop = currentAmountCop,
        progressPercent = progress,
        remainingCop = remaining,
        deadlineDisplay = DateUtil.toDisplayDate(deadlineDate),
        requiredMonthlyCop = requiredMonthly,
        isOverdue = isOverdue,
        colorArgb = colorArgb
    )
}
