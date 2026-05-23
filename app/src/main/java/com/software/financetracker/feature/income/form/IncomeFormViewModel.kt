package com.software.financetracker.feature.income.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.software.financetracker.core.error.Result
import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.data.local.income.IncomeEntity
import com.software.financetracker.domain.repository.IncomeRepository
import com.software.financetracker.feature.income.IncomeSourceType
import com.software.financetracker.feature.income.toIncomeSourceType
import com.software.financetracker.navigation.IncomeFormRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class IncomeFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val incomeRepository: IncomeRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<IncomeFormRoute>()

    private val _state = MutableStateFlow(IncomeFormState())
    val state = _state.asStateFlow()

    private val _events = Channel<IncomeFormEvent>()
    val events = _events.receiveAsFlow()

    init {
        route.incomeId?.let { id ->
            viewModelScope.launch {
                val result = incomeRepository.getById(id)
                if (result is Result.Success) {
                    val e = result.data
                    val sourceType = e.source.toIncomeSourceType()
                    _state.update {
                        it.copy(
                            incomeId = e.id,
                            amountInput = e.amountCop.toString(),
                            selectedSourceType = sourceType,
                            customSource = if (sourceType == IncomeSourceType.OTHER) e.source else "",
                            notes = e.notes,
                            selectedDateStorage = e.date,
                            displayDate = DateUtil.toDisplayDate(e.date)
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: IncomeFormAction) {
        when (action) {
            IncomeFormAction.OnBackClick ->
                viewModelScope.launch { _events.send(IncomeFormEvent.NavigateBack) }
            is IncomeFormAction.OnAmountChange ->
                _state.update { it.copy(amountInput = action.value, amountError = null) }
            is IncomeFormAction.OnSourceTypeSelected ->
                _state.update { it.copy(selectedSourceType = action.type, showSourceDropdown = false) }
            IncomeFormAction.OnSourceDropdownToggle ->
                _state.update { it.copy(showSourceDropdown = !it.showSourceDropdown) }
            IncomeFormAction.OnSourceDropdownDismiss ->
                _state.update { it.copy(showSourceDropdown = false) }
            is IncomeFormAction.OnCustomSourceChange ->
                _state.update { it.copy(customSource = action.value) }
            is IncomeFormAction.OnNotesChange ->
                _state.update { it.copy(notes = action.value) }
            IncomeFormAction.OnDateFieldClick ->
                _state.update { it.copy(showDatePicker = true) }
            is IncomeFormAction.OnDateSelected -> {
                val localDate = Instant.ofEpochMilli(action.epochMillis)
                    .atZone(ZoneOffset.UTC).toLocalDate()
                val stored = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val display = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                _state.update { it.copy(selectedDateStorage = stored, displayDate = display, showDatePicker = false) }
            }
            IncomeFormAction.OnDatePickerDismiss ->
                _state.update { it.copy(showDatePicker = false) }
            IncomeFormAction.OnSaveClick -> save()
            IncomeFormAction.OnDeleteClick ->
                _state.update { it.copy(showDeleteConfirmDialog = true) }
            IncomeFormAction.OnDeleteConfirm -> {
                _state.update { it.copy(showDeleteConfirmDialog = false) }
                delete()
            }
            IncomeFormAction.OnDeleteDismiss ->
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
            val entity = IncomeEntity(
                id = s.incomeId ?: 0L,
                amountCop = amount,
                source = resolvedSource,
                date = s.selectedDateStorage,
                notes = s.notes.trim()
            )
            when (incomeRepository.upsert(entity)) {
                is Result.Success -> _events.send(IncomeFormEvent.NavigateBack)
                is Result.Error -> _events.send(IncomeFormEvent.ShowError(UiText.DynamicString("Error al guardar el ingreso")))
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun delete() {
        val id = _state.value.incomeId ?: return
        viewModelScope.launch {
            val result = incomeRepository.getById(id)
            if (result is Result.Success) {
                incomeRepository.delete(result.data)
                _events.send(IncomeFormEvent.NavigateBack)
            }
        }
    }
}
