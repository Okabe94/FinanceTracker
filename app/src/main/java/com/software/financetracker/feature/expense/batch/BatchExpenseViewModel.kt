package com.software.financetracker.feature.expense.batch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.software.financetracker.core.error.Result
import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.data.local.expense.ExpenseEntity
import com.software.financetracker.domain.repository.CategoryRepository
import com.software.financetracker.domain.repository.ExpenseRepository
import com.software.financetracker.feature.expense.form.CategoryItem
import com.software.financetracker.navigation.BatchExpenseRoute
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

class BatchExpenseViewModel(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<BatchExpenseRoute>()

    private val _state = MutableStateFlow(BatchExpenseState())
    val state = _state.asStateFlow()

    private val _events = Channel<BatchExpenseEvent>()
    val events = _events.receiveAsFlow()

    private var nextRowId = 1

    init {
        categoryRepository.observeAll().onEach { cats ->
            val items = cats.map { CategoryItem(it.id, it.name, it.colorArgb, it.iconKey) }
            _state.update { s ->
                val updatedRows = if (route.categoryId != null) {
                    s.rows.map { row -> row.copy(categoryId = route.categoryId) }
                } else s.rows
                s.copy(categories = items, rows = updatedRows)
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: BatchExpenseAction) {
        when (action) {
            BatchExpenseAction.OnBackClick ->
                viewModelScope.launch { _events.send(BatchExpenseEvent.NavigateBack) }

            BatchExpenseAction.OnAddRow -> {
                val id = nextRowId++
                val defaultCategoryId = if (route.categoryId != null) route.categoryId else null
                _state.update { s ->
                    s.copy(rows = s.rows + ExpenseRowItem(rowId = id, categoryId = defaultCategoryId))
                }
            }

            is BatchExpenseAction.OnRemoveRow ->
                _state.update { s -> s.copy(rows = s.rows.filter { it.rowId != action.rowId }) }

            is BatchExpenseAction.OnAmountChange ->
                _state.update { s ->
                    s.copy(rows = s.rows.map { row ->
                        if (row.rowId == action.rowId) row.copy(amountInput = action.value, amountError = null)
                        else row
                    })
                }

            is BatchExpenseAction.OnCategorySelect ->
                _state.update { s ->
                    s.copy(
                        openDropdownRowId = null,
                        rows = s.rows.map { row ->
                            if (row.rowId == action.rowId) row.copy(categoryId = action.categoryId, categoryError = false)
                            else row
                        }
                    )
                }

            is BatchExpenseAction.OnToggleCategoryDropdown ->
                _state.update { s ->
                    s.copy(openDropdownRowId = if (s.openDropdownRowId == action.rowId) null else action.rowId)
                }

            BatchExpenseAction.OnDismissCategoryDropdown ->
                _state.update { s -> s.copy(openDropdownRowId = null) }

            is BatchExpenseAction.OnDescriptionChange ->
                _state.update { s ->
                    s.copy(rows = s.rows.map { row ->
                        if (row.rowId == action.rowId) row.copy(description = action.value)
                        else row
                    })
                }

            is BatchExpenseAction.OnDateFieldClick ->
                _state.update { s ->
                    s.copy(rows = s.rows.map { row ->
                        if (row.rowId == action.rowId) row.copy(showDatePicker = true)
                        else row.copy(showDatePicker = false)
                    })
                }

            is BatchExpenseAction.OnDateSelected -> {
                val localDate = Instant.ofEpochMilli(action.epochMillis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()
                val stored = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val display = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                _state.update { s ->
                    s.copy(rows = s.rows.map { row ->
                        if (row.rowId == action.rowId) row.copy(
                            dateStorage = stored,
                            displayDate = display,
                            showDatePicker = false
                        ) else row
                    })
                }
            }

            BatchExpenseAction.OnDismissDatePicker ->
                _state.update { s ->
                    s.copy(rows = s.rows.map { it.copy(showDatePicker = false) })
                }

            BatchExpenseAction.OnSaveAll -> saveAll()
        }
    }

    private fun saveAll() {
        val s = _state.value
        var hasError = false

        val validatedRows = s.rows.map { row ->
            val amount = row.amountInput.toLongOrNull()
            val amountErr = if (amount == null || amount <= 0) {
                UiText.DynamicString("Ingresa un monto válido")
            } else null
            val catErr = row.categoryId == null || row.categoryId == 0L

            if (amountErr != null || catErr) hasError = true

            row.copy(amountError = amountErr, categoryError = catErr)
        }

        if (hasError) {
            _state.update { it.copy(rows = validatedRows) }
            return
        }

        val today = DateUtil.today()
        val entities = s.rows.map { row ->
            ExpenseEntity(
                categoryId = row.categoryId!!,
                amountCop = row.amountInput.toLong(),
                description = row.description.trim(),
                date = row.dateStorage.ifEmpty { today }
            )
        }

        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            when (val result = expenseRepository.insertAll(entities)) {
                is Result.Success -> _events.send(BatchExpenseEvent.NavigateBack)
                is Result.Error -> _events.send(
                    BatchExpenseEvent.ShowError(UiText.DynamicString("Error al guardar los gastos"))
                )
            }
            _state.update { it.copy(isSaving = false) }
        }
    }
}
