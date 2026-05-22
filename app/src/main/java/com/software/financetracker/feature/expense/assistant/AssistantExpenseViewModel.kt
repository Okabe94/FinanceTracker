package com.software.financetracker.feature.expense.assistant

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
import com.software.financetracker.navigation.AssistantExpenseRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AssistantExpenseViewModel(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<AssistantExpenseRoute>()

    private val _state = MutableStateFlow(AssistantExpenseState())
    val state = _state.asStateFlow()

    private val _events = Channel<AssistantExpenseEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val categories = categoryRepository.getAll()
            val matched = if (route.categoryName.isNotBlank()) {
                categories.firstOrNull { it.name.contains(route.categoryName, ignoreCase = true) }
            } else null
            _state.update {
                it.copy(
                    isLoading = false,
                    categories = categories,
                    selectedCategory = matched,
                    amountInput = if (route.amountCop > 0) route.amountCop.toString() else ""
                )
            }
        }
    }

    fun onAction(action: AssistantExpenseAction) {
        when (action) {
            AssistantExpenseAction.OnBackClick ->
                viewModelScope.launch { _events.send(AssistantExpenseEvent.NavigateBack) }
            is AssistantExpenseAction.OnCategorySelected ->
                _state.update { it.copy(selectedCategory = action.category, categoryError = null) }
            is AssistantExpenseAction.OnAmountChange ->
                _state.update { it.copy(amountInput = action.value, amountError = null) }
            AssistantExpenseAction.OnSaveClick -> save()
        }
    }

    private fun save() {
        val s = _state.value
        val category = s.selectedCategory
        if (category == null) {
            _state.update { it.copy(categoryError = UiText.DynamicString("Selecciona una categoría")) }
            return
        }
        val amount = s.amountInput.toLongOrNull()
        if (amount == null || amount <= 0) {
            _state.update { it.copy(amountError = UiText.DynamicString("Ingresa un monto válido en pesos")) }
            return
        }
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val entity = ExpenseEntity(
                categoryId = category.id,
                amountCop = amount,
                description = "",
                date = DateUtil.today()
            )
            when (expenseRepository.upsert(entity)) {
                is Result.Success -> _events.send(AssistantExpenseEvent.NavigateBack)
                is Result.Error -> {
                    _state.update { it.copy(isSaving = false) }
                    _events.send(
                        AssistantExpenseEvent.ShowError(UiText.DynamicString("Error al guardar el gasto"))
                    )
                }
            }
        }
    }
}
