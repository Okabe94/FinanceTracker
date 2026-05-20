package com.software.financetracker.feature.investment.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.software.financetracker.core.error.Result
import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.core.util.CurrencyHelper
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.data.local.investment.InvestmentEntryEntity
import com.software.financetracker.domain.model.investment.EntryType
import com.software.financetracker.domain.model.investment.toEntryType
import com.software.financetracker.domain.repository.InvestmentEntryRepository
import com.software.financetracker.domain.repository.InvestmentRepository
import com.software.financetracker.navigation.InvestmentEntryFormRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset

class InvestmentEntryFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val investmentRepository: InvestmentRepository,
    private val entryRepository: InvestmentEntryRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<InvestmentEntryFormRoute>()

    private val _state = MutableStateFlow(
        InvestmentEntryFormState(
            investmentId = route.investmentId,
            dateStorage = DateUtil.today(),
            dateDisplay = DateUtil.toDisplayDate(DateUtil.today())
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<InvestmentEntryFormEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val investmentResult = investmentRepository.getById(route.investmentId)
            if (investmentResult is Result.Success) {
                _state.update { it.copy(investmentCurrency = investmentResult.data.currency) }
            }
        }
        route.entryId?.let { id ->
            viewModelScope.launch {
                val result = entryRepository.getById(id)
                if (result is Result.Success) {
                    val entry = result.data
                    val type = entry.entryType.toEntryType()
                    _state.update {
                        it.copy(
                            entryId = entry.id,
                            selectedType = type,
                            amountInput = CurrencyHelper.toInputString(entry.amountMinorUnits, it.investmentCurrency),
                            showAmountField = type != EntryType.NOTE,
                            dateStorage = entry.date,
                            dateDisplay = DateUtil.toDisplayDate(entry.date),
                            notes = entry.notes
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: InvestmentEntryFormAction) {
        when (action) {
            InvestmentEntryFormAction.OnBackClick ->
                viewModelScope.launch { _events.send(InvestmentEntryFormEvent.NavigateBack) }
            is InvestmentEntryFormAction.OnTypeSelected ->
                _state.update {
                    it.copy(
                        selectedType = action.type,
                        showAmountField = action.type != EntryType.NOTE,
                        amountError = null
                    )
                }
            is InvestmentEntryFormAction.OnAmountChange ->
                _state.update { it.copy(amountInput = action.value, amountError = null) }
            InvestmentEntryFormAction.OnDateClick ->
                _state.update { it.copy(showDatePicker = true) }
            is InvestmentEntryFormAction.OnDateSelected -> {
                val date = Instant.ofEpochMilli(action.dateMillis)
                    .atZone(ZoneOffset.UTC).toLocalDate()
                val storage = date.toString()
                _state.update {
                    it.copy(
                        dateStorage = storage,
                        dateDisplay = DateUtil.toDisplayDate(storage),
                        showDatePicker = false
                    )
                }
            }
            InvestmentEntryFormAction.OnDatePickerDismiss ->
                _state.update { it.copy(showDatePicker = false) }
            is InvestmentEntryFormAction.OnNotesChange ->
                _state.update { it.copy(notes = action.value) }
            InvestmentEntryFormAction.OnSaveClick -> save()
            InvestmentEntryFormAction.OnDeleteClick ->
                _state.update { it.copy(showDeleteDialog = true) }
            InvestmentEntryFormAction.OnDeleteConfirm -> delete()
            InvestmentEntryFormAction.OnDeleteDismiss ->
                _state.update { it.copy(showDeleteDialog = false) }
        }
    }

    private fun save() {
        val s = _state.value
        val amount: Long = if (s.selectedType == EntryType.NOTE) {
            0L
        } else {
            val parsed = CurrencyHelper.parseInput(s.amountInput, s.investmentCurrency)
            if (parsed == null || parsed <= 0L) {
                _state.update { it.copy(amountError = UiText.DynamicString("Ingresa un monto válido")) }
                return
            }
            parsed
        }

        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val entity = InvestmentEntryEntity(
                id = s.entryId ?: 0L,
                investmentId = s.investmentId,
                entryType = s.selectedType.storageKey,
                amountMinorUnits = amount,
                date = s.dateStorage,
                notes = s.notes.trim()
            )
            when (entryRepository.upsert(entity)) {
                is Result.Success -> _events.send(InvestmentEntryFormEvent.NavigateBack)
                is Result.Error -> _events.send(
                    InvestmentEntryFormEvent.ShowError(UiText.DynamicString("Error al guardar"))
                )
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun delete() {
        val id = _state.value.entryId ?: return
        viewModelScope.launch {
            val result = entryRepository.getById(id)
            if (result is Result.Success) {
                entryRepository.delete(result.data)
            }
            _events.send(InvestmentEntryFormEvent.NavigateBack)
        }
    }
}
