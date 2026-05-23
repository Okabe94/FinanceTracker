package com.software.financetracker.feature.investment.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.software.financetracker.core.util.CurrencyHelper
import com.software.financetracker.domain.model.investment.InvestmentMetrics
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

private data class InvestmentWithMetrics(
    val card: InvestmentCardUiModel,
    val currency: String,
    val metrics: InvestmentMetrics
)

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
                            InvestmentWithMetrics(
                                card = InvestmentCardUiModel(
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
                                ),
                                currency = inv.currency,
                                metrics = metrics
                            )
                        }
                    }
                ) { items ->
                    val list = items.toList()
                    val cards = list.map { it.card }
                    val allCop = list.all { it.currency == "COP" }
                    val copItems = list.filter { it.currency == "COP" }
                    val portfolioSummary = if (copItems.isNotEmpty()) {
                        val totalValue = copItems.sumOf { it.metrics.currentValueMinorUnits }
                        val totalInvested = copItems.sumOf { it.metrics.totalInvestedMinorUnits }
                        val returnAmount = totalValue - totalInvested
                        val returnPercent = if (totalInvested > 0L)
                            (returnAmount.toFloat() / totalInvested.toFloat()) * 100f else null
                        PortfolioSummary(
                            totalValueMinorUnits = totalValue,
                            totalInvestedMinorUnits = totalInvested,
                            returnMinorUnits = returnAmount,
                            returnPercent = returnPercent,
                            isCopOnly = allCop
                        )
                    } else null
                    InvestmentListState(
                        isLoading = false,
                        investments = cards,
                        portfolioSummary = portfolioSummary
                    )
                }
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
