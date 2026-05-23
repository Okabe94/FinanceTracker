package com.software.financetracker.feature.income.recurring.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.software.financetracker.core.error.Result
import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.data.local.income.RecurringIncomeEntity
import com.software.financetracker.domain.model.toStorageString
import com.software.financetracker.domain.model.toRecurrenceType
import com.software.financetracker.domain.repository.RecurringIncomeRepository
import com.software.financetracker.feature.income.IncomeSourceType
import com.software.financetracker.feature.income.toIncomeSourceType
import com.software.financetracker.navigation.RecurringIncomeFormRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class RecurringIncomeFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val recurringIncomeRepository: RecurringIncomeRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<RecurringIncomeFormRoute>()

    private val _state = MutableStateFlow(RecurringIncomeFormState())
    val state = _state.asStateFlow()

    private val _events = Channel<RecurringIncomeFormEvent>()
    val events = _events.receiveAsFlow()

    init {
        route.recurringIncomeId?.let { id ->
            viewModelScope.launch {
                val result = recurringIncomeRepository.getById(id)
                if (result is Result.Success) {
                    val e = result.data
                    val sourceType = e.source.toIncomeSourceType()
                    _state.update {
                        it.copy(
                            recurringIncomeId = e.id,
                            amountInput = e.amountCop.toString(),
                            selectedSourceType = sourceType,
                            customSource = if (sourceType == IncomeSourceType.OTHER) e.source else "",
                            notes = e.notes,
                            recurrenceType = e.recurrenceType.toRecurrenceType(),
                            selectedDateStorage = e.startDate,
                            displayDate = DateUtil.toDisplayDate(e.startDate),
                            isActive = e.isActive
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: RecurringIncomeFormAction) {
        when (action) {
            RecurringIncomeFormAction.OnBackClick ->
                viewModelScope.launch { _events.send(RecurringIncomeFormEvent.NavigateBack) }
            is RecurringIncomeFormAction.OnAmountChange ->
                _state.update { it.copy(amountInput = action.value, amountError = null) }
            is RecurringIncomeFormAction.OnSourceTypeSelected ->
                _state.update { it.copy(selectedSourceType = action.type, showSourceDropdown = false) }
            RecurringIncomeFormAction.OnSourceDropdownToggle ->
                _state.update { it.copy(showSourceDropdown = !it.showSourceDropdown) }
            RecurringIncomeFormAction.OnSourceDropdownDismiss ->
                _state.update { it.copy(showSourceDropdown = false) }
            is RecurringIncomeFormAction.OnCustomSourceChange ->
                _state.update { it.copy(customSource = action.value) }
            is RecurringIncomeFormAction.OnNotesChange ->
                _state.update { it.copy(notes = action.value) }
            is RecurringIncomeFormAction.OnRecurrenceTypeChange ->
                _state.update { it.copy(recurrenceType = action.type) }
            RecurringIncomeFormAction.OnDateFieldClick ->
                _state.update { it.copy(showDatePicker = true) }
            is RecurringIncomeFormAction.OnDateSelected -> {
                val localDate = Instant.ofEpochMilli(action.epochMillis)
                    .atZone(ZoneOffset.UTC).toLocalDate()
                val stored = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val display = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                _state.update { it.copy(selectedDateStorage = stored, displayDate = display, showDatePicker = false) }
            }
            RecurringIncomeFormAction.OnDatePickerDismiss ->
                _state.update { it.copy(showDatePicker = false) }
            is RecurringIncomeFormAction.OnActiveToggle ->
                _state.update { it.copy(isActive = action.isActive) }
            RecurringIncomeFormAction.OnSaveClick -> save()
            RecurringIncomeFormAction.OnDeleteClick ->
                _state.update { it.copy(showDeleteConfirmDialog = true) }
            RecurringIncomeFormAction.OnDeleteConfirm -> {
                _state.update { it.copy(showDeleteConfirmDialog = false) }
                delete()
            }
            RecurringIncomeFormAction.OnDeleteDismiss ->
                _state.update { it.copy(showDeleteConfirmDialog = false) }
        }
    }

    private fun save() {
        val s = _state.value
        val amount = s.amountInput.toLongOrNull()
        if (amount == null || amount <= 0) {
            _state.update { it.copy(amountError = UiText.DynamicString("Ingresa un monto válido en pesos")) }
            return
        }
        val resolvedSource = if (s.selectedSourceType == IncomeSourceType.OTHER) s.customSource.trim()
                             else s.selectedSourceType.displayName
        if (resolvedSource.isBlank()) {
            _state.update { it.copy(amountError = UiText.DynamicString("Ingresa la fuente del ingreso")) }
            return
        }
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val existing = s.recurringIncomeId?.let { recurringIncomeRepository.getById(it) }
                ?.let { if (it is Result.Success) it.data else null }
            val entity = RecurringIncomeEntity(
                id = s.recurringIncomeId ?: 0L,
                amountCop = amount,
                source = resolvedSource,
                notes = s.notes.trim(),
                recurrenceType = s.recurrenceType.toStorageString(),
                startDate = s.selectedDateStorage,
                nextDueDate = if (s.recurringIncomeId == null) s.selectedDateStorage
                              else existing?.nextDueDate ?: s.selectedDateStorage,
                isActive = s.isActive
            )
            when (recurringIncomeRepository.upsert(entity)) {
                is Result.Success -> _events.send(RecurringIncomeFormEvent.NavigateBack)
                is Result.Error -> _events.send(
                    RecurringIncomeFormEvent.ShowError(UiText.DynamicString("Error al guardar el ingreso recurrente"))
                )
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun delete() {
        val id = _state.value.recurringIncomeId ?: return
        viewModelScope.launch {
            val result = recurringIncomeRepository.getById(id)
            if (result is Result.Success) {
                recurringIncomeRepository.delete(result.data)
                _events.send(RecurringIncomeFormEvent.NavigateBack)
            }
        }
    }
}
