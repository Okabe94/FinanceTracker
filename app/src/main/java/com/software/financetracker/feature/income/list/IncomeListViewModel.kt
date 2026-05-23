package com.software.financetracker.feature.income.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.software.financetracker.core.error.Result
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.domain.model.displayName
import com.software.financetracker.domain.model.toRecurrenceType
import com.software.financetracker.domain.repository.IncomeRepository
import com.software.financetracker.domain.repository.RecurringIncomeRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IncomeListViewModel(
    private val incomeRepository: IncomeRepository,
    private val recurringIncomeRepository: RecurringIncomeRepository
) : ViewModel() {

    private val _events = Channel<IncomeListEvent>()
    val events = _events.receiveAsFlow()

    val state = combine(
        recurringIncomeRepository.observeActive(),
        incomeRepository.observeInRange("2000-01-01", "2099-12-31")
    ) { templates, entries ->
        val items = buildList {
            templates.forEach { t ->
                add(IncomeItem.Template(
                    id = t.id,
                    amountCop = t.amountCop,
                    source = t.source,
                    recurrenceLabel = t.recurrenceType.toRecurrenceType().displayName(),
                    displayNextDueDate = DateUtil.toDisplayDate(t.nextDueDate)
                ))
            }
            entries.forEach { e ->
                add(IncomeItem.Entry(
                    id = e.id,
                    amountCop = e.amountCop,
                    source = e.source,
                    displayDate = DateUtil.toDisplayDate(e.date),
                    notes = e.notes,
                    isFromTemplate = e.recurringIncomeId != null
                ))
            }
        }
        IncomeListState(items = items, isLoading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), IncomeListState())

    fun onAction(action: IncomeListAction) {
        when (action) {
            IncomeListAction.OnBackClick ->
                viewModelScope.launch { _events.send(IncomeListEvent.NavigateBack) }
            IncomeListAction.OnAddIncomeClick ->
                viewModelScope.launch { _events.send(IncomeListEvent.NavigateToAddIncome) }
            IncomeListAction.OnAddTemplateClick ->
                viewModelScope.launch { _events.send(IncomeListEvent.NavigateToAddTemplate) }
            is IncomeListAction.OnEntryClick ->
                viewModelScope.launch { _events.send(IncomeListEvent.NavigateToEditIncome(action.incomeId)) }
            is IncomeListAction.OnTemplateClick ->
                viewModelScope.launch { _events.send(IncomeListEvent.NavigateToEditTemplate(action.templateId)) }
            is IncomeListAction.OnDeleteClick -> deleteIncome(action.incomeId)
        }
    }

    private fun deleteIncome(id: Long) {
        viewModelScope.launch {
            val result = incomeRepository.getById(id)
            if (result is Result.Success) incomeRepository.delete(result.data)
        }
    }
}
