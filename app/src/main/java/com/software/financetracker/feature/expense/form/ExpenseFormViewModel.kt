package com.software.financetracker.feature.expense.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.software.financetracker.core.error.Result
import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.data.local.expense.ExpenseEntity
import com.software.financetracker.data.local.recurring.RecurringExpenseEntity
import com.software.financetracker.domain.model.RecurrenceType
import com.software.financetracker.domain.model.toRecurrenceType
import com.software.financetracker.domain.model.toStorageString
import com.software.financetracker.domain.repository.CategoryRepository
import com.software.financetracker.domain.repository.ExpenseRepository
import com.software.financetracker.domain.repository.RecurringExpenseRepository
import com.software.financetracker.navigation.ExpenseFormRoute
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

class ExpenseFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val expenseRepository: ExpenseRepository,
    private val recurringExpenseRepository: RecurringExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<ExpenseFormRoute>()

    private val _state = MutableStateFlow(ExpenseFormState(categoryId = route.categoryId))
    val state = _state.asStateFlow()

    private val _events = Channel<ExpenseFormEvent>()
    val events = _events.receiveAsFlow()

    init {
        if (route.categoryId == null) {
            categoryRepository.observeAll().onEach { cats ->
                _state.update { s ->
                    s.copy(categories = cats.map { CategoryItem(it.id, it.name, it.colorArgb, it.iconKey) })
                }
            }.launchIn(viewModelScope)
        }

        route.expenseId?.let { id ->
            viewModelScope.launch {
                val result = expenseRepository.getById(id)
                if (result is Result.Success) {
                    val expense = result.data
                    _state.update {
                        it.copy(
                            expenseId = expense.id,
                            categoryId = expense.categoryId,
                            amountInput = expense.amountCop.toString(),
                            description = expense.description,
                            selectedDateStorage = expense.date,
                            displayDate = DateUtil.toDisplayDate(expense.date)
                        )
                    }
                }
            }
        }

        route.recurringExpenseId?.let { id ->
            viewModelScope.launch {
                val result = recurringExpenseRepository.getById(id)
                if (result is Result.Success) {
                    val e = result.data
                    val recurrenceType = e.recurrenceType.toRecurrenceType()
                    _state.update {
                        it.copy(
                            recurringExpenseId = e.id,
                            categoryId = e.categoryId,
                            amountInput = e.amountCop.toString(),
                            description = e.description,
                            selectedDateStorage = e.startDate,
                            displayDate = DateUtil.toDisplayDate(e.startDate),
                            isRecurring = true,
                            recurrenceType = recurrenceType,
                            customIntervalDays = if (recurrenceType is RecurrenceType.Custom) recurrenceType.intervalDays else 7,
                            isActive = e.isActive
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
                    it.copy(selectedDateStorage = stored, displayDate = display, showDatePicker = false)
                }
            }
            ExpenseFormAction.OnDatePickerDismiss ->
                _state.update { it.copy(showDatePicker = false) }
            ExpenseFormAction.OnToggleRecurring ->
                _state.update { it.copy(isRecurring = !it.isRecurring) }
            is ExpenseFormAction.OnRecurrenceTypeSelect ->
                _state.update { it.copy(recurrenceType = action.type) }
            is ExpenseFormAction.OnCustomIntervalChange -> {
                val days = action.days.toIntOrNull() ?: return
                if (days > 0) _state.update { it.copy(customIntervalDays = days) }
            }
            is ExpenseFormAction.OnCategorySelect ->
                _state.update {
                    it.copy(categoryId = action.id, showCategoryDropdown = false, categoryError = false)
                }
            ExpenseFormAction.OnToggleCategoryDropdown ->
                _state.update { it.copy(showCategoryDropdown = !it.showCategoryDropdown) }
            ExpenseFormAction.OnCategoryDropdownDismiss ->
                _state.update { it.copy(showCategoryDropdown = false) }
            ExpenseFormAction.OnToggleActive ->
                _state.update { it.copy(isActive = !it.isActive) }
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
            _state.update { it.copy(amountError = UiText.DynamicString("Ingresa un monto válido en pesos")) }
            return
        }
        val effectiveCategoryId = s.categoryId
        if (effectiveCategoryId == null || effectiveCategoryId == 0L) {
            _state.update { it.copy(categoryError = true) }
            return
        }
        val recurrenceType = if (s.isRecurring && s.recurrenceType is RecurrenceType.Custom) {
            RecurrenceType.Custom(s.customIntervalDays)
        } else {
            s.recurrenceType
        }
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val result = when {
                s.recurringExpenseId != null -> {
                    val existing = recurringExpenseRepository.getById(s.recurringExpenseId)
                    val nextDue = if (existing is Result.Success) existing.data.nextDueDate
                                  else s.selectedDateStorage
                    recurringExpenseRepository.upsert(
                        RecurringExpenseEntity(
                            id = s.recurringExpenseId,
                            categoryId = effectiveCategoryId,
                            amountCop = amount,
                            description = s.description.trim(),
                            recurrenceType = recurrenceType.toStorageString(),
                            startDate = s.selectedDateStorage,
                            nextDueDate = nextDue,
                            isActive = s.isActive
                        )
                    )
                }
                s.isRecurring -> {
                    recurringExpenseRepository.upsert(
                        RecurringExpenseEntity(
                            id = 0L,
                            categoryId = effectiveCategoryId,
                            amountCop = amount,
                            description = s.description.trim(),
                            recurrenceType = recurrenceType.toStorageString(),
                            startDate = s.selectedDateStorage,
                            nextDueDate = s.selectedDateStorage,
                            isActive = true
                        )
                    )
                }
                else -> {
                    expenseRepository.upsert(
                        ExpenseEntity(
                            id = s.expenseId ?: 0L,
                            categoryId = effectiveCategoryId,
                            amountCop = amount,
                            description = s.description.trim(),
                            date = s.selectedDateStorage
                        )
                    )
                }
            }
            when (result) {
                is Result.Success -> _events.send(ExpenseFormEvent.NavigateBack)
                is Result.Error -> _events.send(
                    ExpenseFormEvent.ShowError(UiText.DynamicString("Error al guardar el gasto"))
                )
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun delete() {
        val s = _state.value
        viewModelScope.launch {
            if (s.recurringExpenseId != null) {
                val result = recurringExpenseRepository.getById(s.recurringExpenseId)
                if (result is Result.Success) {
                    recurringExpenseRepository.delete(result.data)
                    _events.send(ExpenseFormEvent.NavigateBack)
                }
            } else {
                val expId = s.expenseId ?: return@launch
                val result = expenseRepository.getById(expId)
                if (result is Result.Success) {
                    expenseRepository.delete(result.data)
                    _events.send(ExpenseFormEvent.NavigateBack)
                }
            }
        }
    }
}
