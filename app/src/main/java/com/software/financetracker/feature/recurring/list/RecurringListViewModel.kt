package com.software.financetracker.feature.recurring.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.domain.model.toRecurrenceType
import com.software.financetracker.domain.repository.CategoryRepository
import com.software.financetracker.domain.repository.RecurringExpenseRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecurringListViewModel(
    private val recurringExpenseRepository: RecurringExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _events = Channel<RecurringListEvent>()
    val events = _events.receiveAsFlow()

    val state = combine(
        recurringExpenseRepository.observeActive(),
        categoryRepository.observeAll()
    ) { templates, categories ->
        val categoryMap = categories.associateBy { it.id }
        RecurringListState(
            templates = templates.map { t ->
                val cat = categoryMap[t.categoryId]
                RecurringTemplateUi(
                    id = t.id,
                    categoryName = cat?.name ?: "–",
                    categoryColorArgb = cat?.colorArgb ?: 0xFF888888.toInt(),
                    categoryIconKey = cat?.iconKey ?: "",
                    amountCop = t.amountCop,
                    description = t.description,
                    recurrenceType = t.recurrenceType.toRecurrenceType(),
                    displayNextDueDate = DateUtil.toDisplayDate(t.nextDueDate),
                    isActive = t.isActive
                )
            },
            categories = categories.map { c ->
                CategoryPickerItem(
                    id = c.id,
                    name = c.name,
                    iconKey = c.iconKey,
                    colorArgb = c.colorArgb
                )
            },
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), RecurringListState())

    fun onAction(action: RecurringListAction) {
        when (action) {
            RecurringListAction.OnBackClick ->
                viewModelScope.launch { _events.send(RecurringListEvent.NavigateBack) }
            RecurringListAction.OnAddTemplateClick ->
                viewModelScope.launch { _events.send(RecurringListEvent.NavigateToAddTemplate) }
            is RecurringListAction.OnAddExpenseClick ->
                viewModelScope.launch { _events.send(RecurringListEvent.NavigateToAddExpense(action.categoryId)) }
            is RecurringListAction.OnTemplateClick ->
                viewModelScope.launch { _events.send(RecurringListEvent.NavigateToEditForm(action.templateId)) }
        }
    }
}
