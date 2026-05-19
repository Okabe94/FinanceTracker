package com.software.financetracker.feature.recurring.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.software.financetracker.core.error.Result
import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.data.local.recurring.RecurringExpenseEntity
import com.software.financetracker.domain.model.toStorageString
import com.software.financetracker.domain.model.toRecurrenceType
import com.software.financetracker.domain.repository.CategoryRepository
import com.software.financetracker.domain.repository.RecurringExpenseRepository
import com.software.financetracker.navigation.RecurringExpenseFormRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class RecurringExpenseFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val recurringExpenseRepository: RecurringExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<RecurringExpenseFormRoute>()

    private val _state = MutableStateFlow(
        RecurringExpenseFormState(categoryId = route.categoryId)
    )
    val state = _state.asStateFlow()

    private val _events = Channel<RecurringExpenseFormEvent>()
    val events = _events.receiveAsFlow()

    init {
        categoryRepository.observeAll().onEach { cats ->
            _state.update { s ->
                s.copy(categories = cats.map { CategoryItem(it.id, it.name, it.colorArgb, it.iconKey) })
            }
        }.launchIn(viewModelScope)

        route.recurringExpenseId?.let { id ->
            viewModelScope.launch {
                val result = recurringExpenseRepository.getById(id)
                if (result is Result.Success) {
                    val e = result.data
                    _state.update {
                        it.copy(
                            recurringExpenseId = e.id,
                            categoryId = e.categoryId,
                            amountInput = e.amountCop.toString(),
                            description = e.description,
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

    fun onAction(action: RecurringExpenseFormAction) {
        when (action) {
            RecurringExpenseFormAction.OnBackClick ->
                viewModelScope.launch { _events.send(RecurringExpenseFormEvent.NavigateBack) }
            is RecurringExpenseFormAction.OnAmountChange ->
                _state.update { it.copy(amountInput = action.value, amountError = null) }
            is RecurringExpenseFormAction.OnDescriptionChange ->
                _state.update { it.copy(description = action.value) }
            is RecurringExpenseFormAction.OnRecurrenceTypeChange ->
                _state.update { it.copy(recurrenceType = action.type) }
            RecurringExpenseFormAction.OnDateFieldClick ->
                _state.update { it.copy(showDatePicker = true) }
            is RecurringExpenseFormAction.OnDateSelected -> {
                val localDate = Instant.ofEpochMilli(action.epochMillis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()
                val stored = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val display = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                _state.update {
                    it.copy(selectedDateStorage = stored, displayDate = display, showDatePicker = false)
                }
            }
            RecurringExpenseFormAction.OnDatePickerDismiss ->
                _state.update { it.copy(showDatePicker = false) }
            is RecurringExpenseFormAction.OnCategorySelected ->
                _state.update { it.copy(categoryId = action.categoryId, showCategoryDropdown = false, categoryError = false) }
            RecurringExpenseFormAction.OnCategoryDropdownToggle ->
                _state.update { it.copy(showCategoryDropdown = !it.showCategoryDropdown) }
            RecurringExpenseFormAction.OnCategoryDropdownDismiss ->
                _state.update { it.copy(showCategoryDropdown = false) }
            is RecurringExpenseFormAction.OnActiveToggle ->
                _state.update { it.copy(isActive = action.isActive) }
            RecurringExpenseFormAction.OnSaveClick -> save()
            RecurringExpenseFormAction.OnDeleteClick ->
                _state.update { it.copy(showDeleteConfirmDialog = true) }
            RecurringExpenseFormAction.OnDeleteConfirm -> {
                _state.update { it.copy(showDeleteConfirmDialog = false) }
                delete()
            }
            RecurringExpenseFormAction.OnDeleteDismiss ->
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
        if (s.categoryId == 0L) {
            _state.update { it.copy(categoryError = true) }
            return
        }
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val entity = RecurringExpenseEntity(
                id = s.recurringExpenseId ?: 0L,
                categoryId = s.categoryId,
                amountCop = amount,
                description = s.description.trim(),
                recurrenceType = s.recurrenceType.toStorageString(),
                startDate = s.selectedDateStorage,
                nextDueDate = if (s.recurringExpenseId == null) s.selectedDateStorage
                              else recurringExpenseRepository.getById(s.recurringExpenseId)
                                  .let { if (it is Result.Success) it.data.nextDueDate else s.selectedDateStorage },
                isActive = s.isActive
            )
            when (recurringExpenseRepository.upsert(entity)) {
                is Result.Success -> _events.send(RecurringExpenseFormEvent.NavigateBack)
                is Result.Error -> _events.send(
                    RecurringExpenseFormEvent.ShowError(UiText.DynamicString("Error al guardar el gasto recurrente"))
                )
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun delete() {
        val id = _state.value.recurringExpenseId ?: return
        viewModelScope.launch {
            val result = recurringExpenseRepository.getById(id)
            if (result is Result.Success) {
                recurringExpenseRepository.delete(result.data)
                _events.send(RecurringExpenseFormEvent.NavigateBack)
            }
        }
    }
}
