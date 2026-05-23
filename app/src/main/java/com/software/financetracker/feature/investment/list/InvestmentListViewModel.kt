package com.software.financetracker.feature.investment.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.software.financetracker.core.util.CurrencyHelper
import com.software.financetracker.domain.model.investment.InvestmentMetrics
import com.software.financetracker.domain.repository.ExchangeRateRepository
import com.software.financetracker.domain.repository.InvestmentEntryRepository
import com.software.financetracker.domain.repository.InvestmentRepository
import com.software.financetracker.feature.investment.detail.computeMetrics
import com.software.financetracker.ui.components.DonutSlice
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class InvestmentWithMetrics(
    val card: InvestmentCardUiModel,
    val currency: String,
    val metrics: InvestmentMetrics
)

class InvestmentListViewModel(
    private val investmentRepository: InvestmentRepository,
    private val entryRepository: InvestmentEntryRepository,
    private val exchangeRateRepository: ExchangeRateRepository
) : ViewModel() {

    private val _events = Channel<InvestmentListEvent>()
    val events = _events.receiveAsFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _currencyFilter = MutableStateFlow<String?>(null)
    private val _isRefreshingRates = MutableStateFlow(false)
    private val _showRatesBottomSheet = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            try { exchangeRateRepository.refresh() } catch (_: Exception) {}
        }
    }

    private val _rawItems: Flow<List<InvestmentWithMetrics>> =
        investmentRepository.observeAll().flatMapLatest { investments ->
            if (investments.isEmpty()) {
                flowOf(emptyList())
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
                ) { it.toList() }
            }
        }

    val state = combine(
        _rawItems,
        _searchQuery,
        _currencyFilter,
        exchangeRateRepository.getAll()
    ) { rawItems, searchQuery, currencyFilter, rateEntities ->
        val rates = rateEntities.associate { it.fromCurrency to it.rate }
        val ratesUpdatedAt = rateEntities.maxByOrNull { it.updatedDate }?.updatedDate

        val allCop = rawItems.all { it.currency == "COP" }
        val itemsWithCopValue = rawItems.mapNotNull { item ->
            val copValue = CurrencyHelper.convertToCop(item.metrics.currentValueMinorUnits, item.currency, rates)
            val copInvested = CurrencyHelper.convertToCop(item.metrics.totalInvestedMinorUnits, item.currency, rates)
            if (copValue != null && copInvested != null) Triple(item, copValue, copInvested) else null
        }
        val portfolioSummary = if (itemsWithCopValue.isNotEmpty()) {
            val totalValue = itemsWithCopValue.sumOf { it.second }
            val totalInvested = itemsWithCopValue.sumOf { it.third }
            val returnAmount = totalValue - totalInvested
            val returnPercent = if (totalInvested > 0L)
                (returnAmount.toFloat() / totalInvested.toFloat()) * 100f else null
            PortfolioSummary(totalValue, totalInvested, returnAmount, returnPercent, allCop)
        } else null

        val totalCurrentValue = rawItems.sumOf { it.metrics.currentValueMinorUnits.toDouble() }.toFloat()
        val allocationSlices = if (totalCurrentValue > 0f) {
            rawItems
                .filter { it.metrics.currentValueMinorUnits > 0 }
                .sortedByDescending { it.metrics.currentValueMinorUnits }
                .map { item ->
                    DonutSlice(
                        label = item.card.name,
                        fraction = item.metrics.currentValueMinorUnits.toFloat() / totalCurrentValue,
                        colorArgb = item.card.colorArgb
                    )
                }
        } else emptyList()

        val availableCurrencies = rawItems.map { it.currency }.distinct().sorted()

        val filtered = rawItems
            .filter { searchQuery.isBlank() || it.card.name.contains(searchQuery, ignoreCase = true) }
            .filter { currencyFilter == null || it.currency == currencyFilter }

        InvestmentListState(
            isLoading = false,
            investments = filtered.map { it.card },
            portfolioSummary = portfolioSummary,
            allocationSlices = allocationSlices,
            availableCurrencies = availableCurrencies,
            searchQuery = searchQuery,
            activeCurrencyFilter = currencyFilter,
            totalCount = rawItems.size,
            rates = rates,
            ratesUpdatedAt = ratesUpdatedAt
        )
    }.combine(_isRefreshingRates) { s, isRefreshing ->
        s.copy(isRefreshingRates = isRefreshing)
    }.combine(_showRatesBottomSheet) { s, show ->
        s.copy(showRatesBottomSheet = show)
    }.stateIn(
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
            is InvestmentListAction.OnSearchQueryChanged ->
                _searchQuery.update { action.query }
            is InvestmentListAction.OnCurrencyFilterChanged ->
                _currencyFilter.update { action.currency }
            InvestmentListAction.RefreshRates -> {
                viewModelScope.launch {
                    _isRefreshingRates.update { true }
                    try { exchangeRateRepository.refresh() } catch (_: Exception) {}
                    _isRefreshingRates.update { false }
                }
            }
            InvestmentListAction.OnRatesBottomSheetToggled ->
                _showRatesBottomSheet.update { !it }
        }
    }
}
