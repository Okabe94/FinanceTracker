package com.software.financetracker.feature.investment.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.software.financetracker.core.util.CurrencyHelper
import com.software.financetracker.domain.repository.InvestmentEntryRepository
import com.software.financetracker.domain.repository.InvestmentRepository
import com.software.financetracker.feature.investment.detail.computeMetrics
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InvestmentListViewModel(
    private val investmentRepository: InvestmentRepository,
    private val entryRepository: InvestmentEntryRepository
) : ViewModel() {

    private val _events = Channel<InvestmentListEvent>()
    val events = _events.receiveAsFlow()

    val state = investmentRepository.observeAll()
        .flatMapLatest { investments ->
            if (investments.isEmpty()) {
                flowOf(InvestmentListState(isLoading = false, investments = emptyList()))
            } else {
                combine(
                    investments.map { inv ->
                        entryRepository.observeByInvestment(inv.id).map { entries ->
                            val sortedAsc = entries.sortedWith(compareBy({ it.date }, { it.id }))
                            val metrics = computeMetrics(inv, sortedAsc)
                            InvestmentCardUiModel(
                                id = inv.id,
                                name = inv.name,
                                currency = inv.currency,
                                colorArgb = inv.colorArgb,
                                iconKey = inv.iconKey,
                                currentValueFormatted = CurrencyHelper.format(
                                    metrics.currentValueMinorUnits, inv.currency
                                ),
                                returnPercent = metrics.returnPercent,
                                isPositiveReturn = metrics.returnMinorUnits >= 0
                            )
                        }
                    }
                ) { cards -> InvestmentListState(isLoading = false, investments = cards.toList()) }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            InvestmentListState()
        )

    fun onAction(action: InvestmentListAction) {
        when (action) {
            InvestmentListAction.OnAddClick ->
                viewModelScope.launch { _events.send(InvestmentListEvent.NavigateToAddForm) }
            is InvestmentListAction.OnCardClick ->
                viewModelScope.launch {
                    _events.send(InvestmentListEvent.NavigateToDetail(action.investmentId))
                }
        }
    }
}
