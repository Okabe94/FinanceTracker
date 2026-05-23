package com.software.financetracker.feature.income.recurring.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.domain.model.toRecurrenceType
import com.software.financetracker.domain.repository.RecurringIncomeRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecurringIncomeListViewModel(
    private val recurringIncomeRepository: RecurringIncomeRepository
) : ViewModel() {

    private val _events = Channel<RecurringIncomeListEvent>()
    val events = _events.receiveAsFlow()

    val state = recurringIncomeRepository.observeActive()
        .map { templates ->
            RecurringIncomeListState(
                templates = templates.map { t ->
                    RecurringIncomeTemplateUi(
                        id = t.id,
                        amountCop = t.amountCop,
                        source = t.source,
                        recurrenceType = t.recurrenceType.toRecurrenceType(),
                        displayNextDueDate = DateUtil.toDisplayDate(t.nextDueDate),
                        isActive = t.isActive
                    )
                },
                isLoading = false
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), RecurringIncomeListState())

    fun onAction(action: RecurringIncomeListAction) {
        when (action) {
            RecurringIncomeListAction.OnBackClick ->
                viewModelScope.launch { _events.send(RecurringIncomeListEvent.NavigateBack) }
            RecurringIncomeListAction.OnAddClick ->
                viewModelScope.launch { _events.send(RecurringIncomeListEvent.NavigateToAddForm) }
            is RecurringIncomeListAction.OnTemplateClick ->
                viewModelScope.launch { _events.send(RecurringIncomeListEvent.NavigateToEditForm(action.templateId)) }
        }
    }
}
