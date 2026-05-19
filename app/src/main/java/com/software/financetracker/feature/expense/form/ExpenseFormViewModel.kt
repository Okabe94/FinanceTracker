package com.software.financetracker.feature.expense.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.software.financetracker.core.error.Result
import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.data.local.expense.ExpenseEntity
import com.software.financetracker.domain.repository.ExpenseRepository
import com.software.financetracker.navigation.ExpenseFormRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ExpenseFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<ExpenseFormRoute>()

    private val _state = MutableStateFlow(ExpenseFormState(categoryId = route.categoryId))
    val state = _state.asStateFlow()

    private val _events = Channel<ExpenseFormEvent>()
    val events = _events.receiveAsFlow()

    init {
        route.expenseId?.let { id ->
            viewModelScope.launch {
                val result = expenseRepository.getById(id)
                if (result is Result.Success) {
                    val expense = result.data
                    _state.update {
                        it.copy(
                            expenseId = expense.id,
                            amountInput = expense.amountCop.toString(),
                            description = expense.description,
                            selectedDateStorage = expense.date,
                            displayDate = DateUtil.toDisplayDate(expense.date)
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: ExpenseFormAction) {
        when (action) {
            ExpenseFormAction.OnBackClick ->
                viewModelScope.launch { _events.send(ExpenseFormEvent.NavigateBack) }
            is ExpenseFormAction.OnAmountChange ->
                _state.update { it.copy(amountInput = action.value, amountError = null) }
            is ExpenseFormAction.OnDescriptionChange ->
                _state.update { it.copy(description = action.value) }
            ExpenseFormAction.OnDateFieldClick ->
                _state.update { it.copy(showDatePicker = true) }
            is ExpenseFormAction.OnDateSelected -> {
                val localDate = Instant.ofEpochMilli(action.epochMillis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()
                val stored = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val display = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                _state.update {
                    it.copy(
                        selectedDateStorage = stored,
                        displayDate = display,
                        showDatePicker = false
                    )
                }
            }
            ExpenseFormAction.OnDatePickerDismiss ->
                _state.update { it.copy(showDatePicker = false) }
            ExpenseFormAction.OnSaveClick -> save()
            ExpenseFormAction.OnDeleteClick ->
                _state.update { it.copy(showDeleteConfirmDialog = true) }
            ExpenseFormAction.OnDeleteConfirm -> {
                _state.update { it.copy(showDeleteConfirmDialog = false) }
                delete()
            }
            ExpenseFormAction.OnDeleteDismiss ->
                _state.update { it.copy(showDeleteConfirmDialog = false) }
        }
    }

    private fun save() {
        val s = _state.value
        val amount = s.amountInput.toLongOrNull()
        if (amount == null || amount <= 0) {
            _state.update {
                it.copy(amountError = UiText.DynamicString("Ingresa un monto válido en pesos"))
            }
            return
        }
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val entity = ExpenseEntity(
                id = s.expenseId ?: 0L,
                categoryId = s.categoryId,
                amountCop = amount,
                description = s.description.trim(),
                date = s.selectedDateStorage
            )
            when (expenseRepository.upsert(entity)) {
                is Result.Success -> _events.send(ExpenseFormEvent.NavigateBack)
                is Result.Error -> _events.send(
                    ExpenseFormEvent.ShowError(UiText.DynamicString("Error al guardar el gasto"))
                )
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun delete() {
        val expId = _state.value.expenseId ?: return
        viewModelScope.launch {
            val result = expenseRepository.getById(expId)
            if (result is Result.Success) {
                expenseRepository.delete(result.data)
                _events.send(ExpenseFormEvent.NavigateBack)
            }
        }
    }
}
