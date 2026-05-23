package com.software.financetracker.feature.goal.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.software.financetracker.core.error.Result
import com.software.financetracker.domain.repository.GoalRepository
import com.software.financetracker.feature.goal.list.toUiModel
import com.software.financetracker.navigation.GoalDetailRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GoalDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<GoalDetailRoute>()
    private val _contributionInput = MutableStateFlow("")
    private val _showContributionDialog = MutableStateFlow(false)

    private val _events = Channel<GoalDetailEvent>()
    val events = _events.receiveAsFlow()

    val state = combine(
        goalRepository.observeActive(),
        goalRepository.observeAchieved(),
        _contributionInput,
        _showContributionDialog
    ) { active, achieved, contribution, showDialog ->
        val all = active + achieved
        val goal = all.find { it.id == route.goalId }
        GoalDetailState(
            goal = goal?.toUiModel(),
            contributionInput = contribution,
            showContributionDialog = showDialog,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), GoalDetailState())

    fun onAction(action: GoalDetailAction) {
        when (action) {
            GoalDetailAction.OnBackClick ->
                viewModelScope.launch { _events.send(GoalDetailEvent.NavigateBack) }
            GoalDetailAction.OnEditClick ->
                viewModelScope.launch { _events.send(GoalDetailEvent.NavigateToEdit(route.goalId)) }
            GoalDetailAction.OnAddContributionClick ->
                _showContributionDialog.update { true }
            is GoalDetailAction.OnContributionChange ->
                _contributionInput.update { action.value }
            GoalDetailAction.OnContributionDismiss -> {
                _showContributionDialog.update { false }
                _contributionInput.update { "" }
            }
            GoalDetailAction.OnContributionConfirm -> addContribution()
            GoalDetailAction.OnMarkAchievedClick -> markAchieved()
        }
    }

    private fun addContribution() {
        val amount = _contributionInput.value.toLongOrNull() ?: return
        if (amount <= 0) return
        viewModelScope.launch {
            val result = goalRepository.getById(route.goalId)
            if (result is Result.Success) {
                val updated = result.data.copy(currentAmountCop = result.data.currentAmountCop + amount)
                goalRepository.upsert(updated)
                _showContributionDialog.update { false }
                _contributionInput.update { "" }
            }
        }
    }

    private fun markAchieved() {
        viewModelScope.launch {
            val result = goalRepository.getById(route.goalId)
            if (result is Result.Success) {
                goalRepository.upsert(result.data.copy(isAchieved = true))
            }
        }
    }
}
