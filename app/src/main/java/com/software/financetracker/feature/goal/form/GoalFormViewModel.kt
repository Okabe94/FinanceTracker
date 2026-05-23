package com.software.financetracker.feature.goal.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.software.financetracker.core.error.Result
import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.data.local.goal.GoalEntity
import com.software.financetracker.domain.repository.GoalRepository
import com.software.financetracker.navigation.GoalFormRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class GoalFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<GoalFormRoute>()

    private val _state = MutableStateFlow(GoalFormState())
    val state = _state.asStateFlow()

    private val _events = Channel<GoalFormEvent>()
    val events = _events.receiveAsFlow()

    init {
        route.goalId?.let { id ->
            viewModelScope.launch {
                val result = goalRepository.getById(id)
                if (result is Result.Success) {
                    val g = result.data
                    _state.update {
                        it.copy(
                            goalId = g.id,
                            name = g.name,
                            targetAmountInput = g.targetAmountCop.toString(),
                            selectedDateStorage = g.deadlineDate,
                            displayDate = DateUtil.toDisplayDate(g.deadlineDate),
                            selectedColorArgb = g.colorArgb
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: GoalFormAction) {
        when (action) {
            GoalFormAction.OnBackClick ->
                viewModelScope.launch { _events.send(GoalFormEvent.NavigateBack) }
            is GoalFormAction.OnNameChange ->
                _state.update { it.copy(name = action.value, nameError = false) }
            is GoalFormAction.OnTargetAmountChange ->
                _state.update { it.copy(targetAmountInput = action.value, targetAmountError = null) }
            GoalFormAction.OnDateFieldClick ->
                _state.update { it.copy(showDatePicker = true) }
            is GoalFormAction.OnDateSelected -> {
                val localDate = Instant.ofEpochMilli(action.epochMillis)
                    .atZone(ZoneOffset.UTC).toLocalDate()
                val stored = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val display = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                _state.update { it.copy(selectedDateStorage = stored, displayDate = display, showDatePicker = false) }
            }
            GoalFormAction.OnDatePickerDismiss ->
                _state.update { it.copy(showDatePicker = false) }
            is GoalFormAction.OnColorSelected ->
                _state.update { it.copy(selectedColorArgb = action.colorArgb) }
            GoalFormAction.OnSaveClick -> save()
            GoalFormAction.OnDeleteClick ->
                _state.update { it.copy(showDeleteConfirmDialog = true) }
            GoalFormAction.OnDeleteConfirm -> {
                _state.update { it.copy(showDeleteConfirmDialog = false) }
                delete()
            }
            GoalFormAction.OnDeleteDismiss ->
                _state.update { it.copy(showDeleteConfirmDialog = false) }
        }
    }

    private fun save() {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(nameError = true) }
            return
        }
        val amount = s.targetAmountInput.toLongOrNull()
        if (amount == null || amount <= 0) {
            _state.update { it.copy(targetAmountError = UiText.DynamicString("Ingresa un monto válido en pesos")) }
            return
        }
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val existing = s.goalId?.let { goalRepository.getById(it) }
                ?.let { if (it is Result.Success) it.data else null }
            val entity = GoalEntity(
                id = s.goalId ?: 0L,
                name = s.name.trim(),
                targetAmountCop = amount,
                currentAmountCop = existing?.currentAmountCop ?: 0L,
                deadlineDate = s.selectedDateStorage,
                colorArgb = s.selectedColorArgb,
                isAchieved = existing?.isAchieved ?: false
            )
            when (goalRepository.upsert(entity)) {
                is Result.Success -> _events.send(GoalFormEvent.NavigateBack)
                is Result.Error -> _events.send(GoalFormEvent.ShowError(UiText.DynamicString("Error al guardar la meta")))
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun delete() {
        val id = _state.value.goalId ?: return
        viewModelScope.launch {
            val result = goalRepository.getById(id)
            if (result is Result.Success) {
                goalRepository.delete(result.data)
                _events.send(GoalFormEvent.NavigateBack)
            }
        }
    }
}
