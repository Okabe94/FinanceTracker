package com.software.financetracker.feature.investment.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.software.financetracker.core.error.Result
import com.software.financetracker.core.preferences.UserPreferences
import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.core.util.CurrencyHelper
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.data.local.investment.InvestmentEntity
import com.software.financetracker.domain.repository.InvestmentRepository
import com.software.financetracker.navigation.InvestmentFormRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset

class InvestmentFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val investmentRepository: InvestmentRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    private val route = savedStateHandle.toRoute<InvestmentFormRoute>()

    private val _state = MutableStateFlow(InvestmentFormState())
    val state = _state.asStateFlow()

    private val _events = Channel<InvestmentFormEvent>()
    val events = _events.receiveAsFlow()

    init {
        if (route.investmentId == null) {
            viewModelScope.launch {
                val currency = prefs.defaultCurrency.first()
                _state.update { it.copy(selectedCurrency = currency) }
            }
        }

        route.investmentId?.let { id ->
            viewModelScope.launch {
                val result = investmentRepository.getById(id)
                if (result is Result.Success) {
                    val inv = result.data
                    _state.update {
                        it.copy(
                            investmentId = inv.id,
                            name = inv.name,
                            selectedCurrency = inv.currency,
                            selectedColorArgb = inv.colorArgb,
                            selectedIconKey = inv.iconKey,
                            hasFixedRoi = inv.annualRatePercent != null,
                            annualRateInput = inv.annualRatePercent?.toString() ?: "",
                            maturityDateStorage = inv.maturityDate,
                            maturityDateDisplay = inv.maturityDate?.let { d -> DateUtil.toDisplayDate(d) },
                            targetEnabled = inv.targetValueMinorUnits != null,
                            targetValueInput = inv.targetValueMinorUnits
                                ?.let { v -> CurrencyHelper.toInputString(v, inv.currency) } ?: "",
                            targetDateStorage = inv.targetDate,
                            targetDateDisplay = inv.targetDate?.let { d -> DateUtil.toDisplayDate(d) }
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: InvestmentFormAction) {
        when (action) {
            InvestmentFormAction.OnBackClick ->
                viewModelScope.launch { _events.send(InvestmentFormEvent.NavigateBack) }
            is InvestmentFormAction.OnNameChange ->
                _state.update { it.copy(name = action.value, nameError = null) }
            is InvestmentFormAction.OnColorSelected ->
                _state.update { it.copy(selectedColorArgb = action.argb) }
            is InvestmentFormAction.OnIconSelected ->
                _state.update { it.copy(selectedIconKey = action.key) }
            is InvestmentFormAction.OnCurrencySelected ->
                _state.update { it.copy(selectedCurrency = action.currency, showCurrencyDropdown = false) }
            InvestmentFormAction.OnCurrencyDropdownToggle ->
                _state.update { it.copy(showCurrencyDropdown = !it.showCurrencyDropdown) }
            is InvestmentFormAction.OnFixedRoiToggle ->
                _state.update { it.copy(hasFixedRoi = action.enabled, annualRateError = null) }
            is InvestmentFormAction.OnAnnualRateChange ->
                _state.update { it.copy(annualRateInput = action.value, annualRateError = null) }
            InvestmentFormAction.OnMaturityDateClick ->
                _state.update { it.copy(showMaturityDatePicker = true) }
            is InvestmentFormAction.OnMaturityDateSelected -> {
                val date = Instant.ofEpochMilli(action.dateMillis)
                    .atZone(ZoneOffset.UTC).toLocalDate()
                val storage = date.toString()
                val display = DateUtil.toDisplayDate(storage)
                _state.update {
                    it.copy(
                        maturityDateStorage = storage,
                        maturityDateDisplay = display,
                        showMaturityDatePicker = false
                    )
                }
            }
            InvestmentFormAction.OnMaturityDatePickerDismiss ->
                _state.update { it.copy(showMaturityDatePicker = false) }
            InvestmentFormAction.OnSaveClick -> save()
            InvestmentFormAction.OnDeleteClick ->
                _state.update { it.copy(showDeleteDialog = true) }
            InvestmentFormAction.OnDeleteConfirm -> delete()
            InvestmentFormAction.OnDeleteDismiss ->
                _state.update { it.copy(showDeleteDialog = false) }
            InvestmentFormAction.OnTargetEnabledToggled ->
                _state.update { it.copy(targetEnabled = !it.targetEnabled, targetValueError = null) }
            is InvestmentFormAction.OnTargetValueChanged ->
                _state.update { it.copy(targetValueInput = action.value, targetValueError = null) }
            InvestmentFormAction.OnTargetDateClick ->
                _state.update { it.copy(showTargetDatePicker = true) }
            is InvestmentFormAction.OnTargetDateSelected -> {
                val date = Instant.ofEpochMilli(action.dateMillis)
                    .atZone(ZoneOffset.UTC).toLocalDate()
                val storage = date.toString()
                _state.update {
                    it.copy(
                        targetDateStorage = storage,
                        targetDateDisplay = DateUtil.toDisplayDate(storage),
                        showTargetDatePicker = false
                    )
                }
            }
            InvestmentFormAction.OnTargetDatePickerDismiss ->
                _state.update { it.copy(showTargetDatePicker = false) }
        }
    }

    private fun save() {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(nameError = UiText.DynamicString("El nombre es requerido")) }
            return
        }
        val rate: Double? = if (s.hasFixedRoi) {
            val r = s.annualRateInput.replace(",", ".").toDoubleOrNull()
            if (r == null || r <= 0.0) {
                _state.update { it.copy(annualRateError = UiText.DynamicString("Ingresa una tasa válida")) }
                return
            }
            r
        } else null

        val targetValue: Long? = if (s.targetEnabled && s.targetValueInput.isNotBlank()) {
            val parsed = CurrencyHelper.parseInput(s.targetValueInput, s.selectedCurrency)
            if (parsed == null || parsed <= 0L) {
                _state.update { it.copy(targetValueError = UiText.DynamicString("Ingresa un valor válido")) }
                return
            }
            parsed
        } else null

        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val entity = InvestmentEntity(
                id = s.investmentId ?: 0L,
                name = s.name.trim(),
                currency = s.selectedCurrency,
                colorArgb = s.selectedColorArgb,
                iconKey = s.selectedIconKey,
                annualRatePercent = rate,
                maturityDate = if (s.hasFixedRoi) s.maturityDateStorage else null,
                createdDate = DateUtil.today(),
                targetValueMinorUnits = targetValue,
                targetDate = if (s.targetEnabled) s.targetDateStorage else null
            )
            when (investmentRepository.upsert(entity)) {
                is Result.Success -> _events.send(InvestmentFormEvent.NavigateBack)
                is Result.Error -> _events.send(
                    InvestmentFormEvent.ShowError(UiText.DynamicString("Error al guardar"))
                )
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun delete() {
        val id = _state.value.investmentId ?: return
        viewModelScope.launch {
            val result = investmentRepository.getById(id)
            if (result is Result.Success) {
                investmentRepository.delete(result.data)
            }
            _events.send(InvestmentFormEvent.NavigateBack)
        }
    }
}
