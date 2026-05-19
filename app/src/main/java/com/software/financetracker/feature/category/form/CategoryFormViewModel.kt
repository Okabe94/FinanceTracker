package com.software.financetracker.feature.category.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.software.financetracker.core.error.Result
import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.data.local.category.CategoryEntity
import com.software.financetracker.domain.repository.CategoryRepository
import com.software.financetracker.navigation.CategoryFormRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<CategoryFormRoute>()

    private val _state = MutableStateFlow(CategoryFormState())
    val state = _state.asStateFlow()

    private val _events = Channel<CategoryFormEvent>()
    val events = _events.receiveAsFlow()

    init {
        route.categoryId?.let { id ->
            viewModelScope.launch {
                val result = categoryRepository.getById(id)
                if (result is Result.Success) {
                    val cat = result.data
                    _state.update {
                        it.copy(
                            categoryId = cat.id,
                            name = cat.name,
                            selectedColorArgb = cat.colorArgb,
                            selectedIconKey = cat.iconKey,
                            hasLimit = cat.monthlyLimitCop != null,
                            limitInputCop = cat.monthlyLimitCop?.toString() ?: ""
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: CategoryFormAction) {
        when (action) {
            CategoryFormAction.OnBackClick ->
                viewModelScope.launch { _events.send(CategoryFormEvent.NavigateBack) }
            is CategoryFormAction.OnNameChange ->
                _state.update { it.copy(name = action.value, nameError = null) }
            is CategoryFormAction.OnColorSelected ->
                _state.update { it.copy(selectedColorArgb = action.argb) }
            is CategoryFormAction.OnIconSelected ->
                _state.update { it.copy(selectedIconKey = action.key) }
            is CategoryFormAction.OnLimitToggle ->
                _state.update { it.copy(hasLimit = action.enabled, limitError = null) }
            is CategoryFormAction.OnLimitAmountChange ->
                _state.update { it.copy(limitInputCop = action.value, limitError = null) }
            CategoryFormAction.OnSaveClick -> save()
        }
    }

    private fun save() {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(nameError = UiText.DynamicString("El nombre es requerido")) }
            return
        }
        val limit: Long? = if (s.hasLimit) {
            val l = s.limitInputCop.toLongOrNull()
            if (l == null || l <= 0) {
                _state.update { it.copy(limitError = UiText.DynamicString("Ingresa un monto válido")) }
                return
            }
            l
        } else null

        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val entity = CategoryEntity(
                id = s.categoryId ?: 0L,
                name = s.name.trim(),
                colorArgb = s.selectedColorArgb,
                iconKey = s.selectedIconKey,
                monthlyLimitCop = limit
            )
            when (categoryRepository.upsert(entity)) {
                is Result.Success -> _events.send(CategoryFormEvent.NavigateBack)
                is Result.Error -> _events.send(
                    CategoryFormEvent.ShowError(UiText.DynamicString("Error al guardar"))
                )
            }
            _state.update { it.copy(isSaving = false) }
        }
    }
}
