package com.software.financetracker.feature.investment.entry.batch

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
import com.software.financetracker.domain.repository.InvestmentEntryRepository
import com.software.financetracker.navigation.BatchInvestmentEntryRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset

class BatchInvestmentEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val entryRepository: InvestmentEntryRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<BatchInvestmentEntryRoute>()

    private val _state = MutableStateFlow(
        BatchInvestmentEntryState(
            investmentId = route.investmentId,
            investmentCurrency = route.investmentCurrency
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<BatchInvestmentEntryEvent>()
    val events = _events.receiveAsFlow()

    private var nextRowId = 1

    fun onAction(action: BatchInvestmentEntryAction) {
        when (action) {
            BatchInvestmentEntryAction.OnBackClick ->
                viewModelScope.launch { _events.send(BatchInvestmentEntryEvent.NavigateBack) }

            BatchInvestmentEntryAction.OnAddRow -> {
                val id = nextRowId++
                _state.update { s -> s.copy(rows = s.rows + InvestmentEntryRowItem(rowId = id)) }
            }

            is BatchInvestmentEntryAction.OnRemoveRow ->
                _state.update { s -> s.copy(rows = s.rows.filter { it.rowId != action.rowId }) }

            is BatchInvestmentEntryAction.OnTypeSelected ->
                _state.update { s ->
                    s.copy(rows = s.rows.map { row ->
                        if (row.rowId == action.rowId) row.copy(selectedType = action.type, amountError = null)
                        else row
                    })
                }

            is BatchInvestmentEntryAction.OnAmountChange ->
                _state.update { s ->
                    s.copy(rows = s.rows.map { row ->
                        if (row.rowId == action.rowId) row.copy(amountInput = action.value, amountError = null)
                        else row
                    })
                }

            is BatchInvestmentEntryAction.OnDateFieldClick ->
                _state.update { s ->
                    s.copy(rows = s.rows.map { row ->
                        if (row.rowId == action.rowId) row.copy(showDatePicker = true)
                        else row.copy(showDatePicker = false)
                    })
                }

            is BatchInvestmentEntryAction.OnDateSelected -> {
                val date = Instant.ofEpochMilli(action.epochMillis)
                    .atZone(ZoneOffset.UTC).toLocalDate()
                val storage = date.toString()
                val display = DateUtil.toDisplayDate(storage)
                _state.update { s ->
                    s.copy(rows = s.rows.map { row ->
                        if (row.rowId == action.rowId) row.copy(
                            dateStorage = storage,
                            displayDate = display,
                            showDatePicker = false
                        ) else row
                    })
                }
            }

            BatchInvestmentEntryAction.OnDismissDatePicker ->
                _state.update { s ->
                    s.copy(rows = s.rows.map { it.copy(showDatePicker = false) })
                }

            is BatchInvestmentEntryAction.OnNotesChange ->
                _state.update { s ->
                    s.copy(rows = s.rows.map { row ->
                        if (row.rowId == action.rowId) row.copy(notes = action.value)
                        else row
                    })
                }

            BatchInvestmentEntryAction.OnSaveAll -> saveAll()
        }
    }

    private fun saveAll() {
        val s = _state.value
        var hasError = false

        val validatedRows = s.rows.map { row ->
            if (row.selectedType == EntryType.NOTE) return@map row
            val parsed = CurrencyHelper.parseInput(row.amountInput, s.investmentCurrency)
            val amountErr = if (parsed == null || parsed <= 0L) {
                UiText.DynamicString("Ingresa un monto válido")
            } else null
            if (amountErr != null) hasError = true
            row.copy(amountError = amountErr)
        }

        if (hasError) {
            _state.update { it.copy(rows = validatedRows) }
            return
        }

        val today = DateUtil.today()
        val entities = s.rows.map { row ->
            val amount = if (row.selectedType == EntryType.NOTE) 0L
            else CurrencyHelper.parseInput(row.amountInput, s.investmentCurrency) ?: 0L
            InvestmentEntryEntity(
                investmentId = s.investmentId,
                entryType = row.selectedType.storageKey,
                amountMinorUnits = amount,
                date = row.dateStorage.ifEmpty { today },
                notes = row.notes.trim()
            )
        }

        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            when (entryRepository.insertAll(entities)) {
                is Result.Success -> _events.send(BatchInvestmentEntryEvent.NavigateBack)
                is Result.Error -> _events.send(
                    BatchInvestmentEntryEvent.ShowError(UiText.DynamicString("Error al guardar las entradas"))
                )
            }
            _state.update { it.copy(isSaving = false) }
        }
    }
}
