package com.software.financetracker.feature.investment.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.software.financetracker.core.error.Result
import com.software.financetracker.core.util.CurrencyHelper
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.data.local.investment.InvestmentEntryEntity
import com.software.financetracker.data.local.investment.InvestmentEntity
import com.software.financetracker.domain.model.investment.EntryType
import com.software.financetracker.domain.model.investment.InvestmentMetrics
import com.software.financetracker.domain.model.investment.toEntryType
import com.software.financetracker.domain.repository.InvestmentEntryRepository
import com.software.financetracker.domain.repository.InvestmentRepository
import com.software.financetracker.navigation.InvestmentDetailRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.pow

class InvestmentDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val investmentRepository: InvestmentRepository,
    private val entryRepository: InvestmentEntryRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<InvestmentDetailRoute>()

    private val _events = Channel<InvestmentDetailEvent>()
    val events = _events.receiveAsFlow()

    private val _showDeleteDialog = MutableStateFlow(false)

    val state = combine(
        investmentRepository.observeAll().map { list -> list.find { it.id == route.investmentId } },
        entryRepository.observeByInvestment(route.investmentId),
        _showDeleteDialog
    ) { investment, entries, showDelete ->
        if (investment == null) {
            InvestmentDetailState(isLoading = false, showDeleteInvestmentDialog = showDelete)
        } else {
            buildState(investment, entries).copy(showDeleteInvestmentDialog = showDelete)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        InvestmentDetailState()
    )

    fun onAction(action: InvestmentDetailAction) {
        when (action) {
            InvestmentDetailAction.OnBackClick ->
                viewModelScope.launch { _events.send(InvestmentDetailEvent.NavigateBack) }
            InvestmentDetailAction.OnEditClick ->
                viewModelScope.launch {
                    _events.send(InvestmentDetailEvent.NavigateToEditInvestment(route.investmentId))
                }
            InvestmentDetailAction.OnAddEntryClick ->
                viewModelScope.launch {
                    _events.send(InvestmentDetailEvent.NavigateToAddEntry(route.investmentId))
                }
            is InvestmentDetailAction.OnEntryClick ->
                viewModelScope.launch {
                    _events.send(
                        InvestmentDetailEvent.NavigateToEditEntry(route.investmentId, action.entryId)
                    )
                }
            InvestmentDetailAction.OnDeleteInvestmentClick ->
                _showDeleteDialog.update { true }
            InvestmentDetailAction.OnDeleteInvestmentConfirm -> deleteInvestment()
            InvestmentDetailAction.OnDeleteInvestmentDismiss ->
                _showDeleteDialog.update { false }
        }
    }

    private fun deleteInvestment() {
        viewModelScope.launch {
            val result = investmentRepository.getById(route.investmentId)
            if (result is Result.Success) {
                investmentRepository.delete(result.data)
            }
            _events.send(InvestmentDetailEvent.NavigateBack)
        }
    }

    private fun buildState(
        investment: InvestmentEntity,
        entries: List<InvestmentEntryEntity>
    ): InvestmentDetailState {
        val sortedAsc = entries.sortedWith(compareBy({ it.date }, { it.id }))
        val metrics = computeMetrics(investment, sortedAsc)
        val currency = investment.currency

        val snapshots = sortedAsc
            .filter { it.entryType == EntryType.VALUE_SNAPSHOT.storageKey }
            .map { SnapshotPoint(DateUtil.toDisplayDate(it.date), it.amountMinorUnits) }

        val entryUiModels = entries.map { entry ->
            val type = entry.entryType.toEntryType()
            EntryUiModel(
                id = entry.id,
                typeLabel = type.labelEs,
                typeColor = entryTypeColor(type),
                amountFormatted = if (type == EntryType.NOTE) null
                else CurrencyHelper.format(entry.amountMinorUnits, currency),
                dateDisplay = DateUtil.toDisplayDate(entry.date),
                notes = entry.notes
            )
        }

        val returnSign = if (metrics.returnMinorUnits >= 0) "+" else ""
        val returnPercentStr = metrics.returnPercent?.let { String.format("%.1f%%", it) } ?: "–"

        return InvestmentDetailState(
            isLoading = false,
            investmentId = investment.id,
            investmentName = investment.name,
            currency = currency,
            colorArgb = investment.colorArgb,
            iconKey = investment.iconKey,
            annualRatePercent = investment.annualRatePercent,
            maturityDateDisplay = investment.maturityDate?.let { DateUtil.toDisplayDate(it) },
            currentValueFormatted = CurrencyHelper.format(metrics.currentValueMinorUnits, currency),
            totalInvestedFormatted = CurrencyHelper.format(metrics.totalInvestedMinorUnits, currency),
            returnFormatted = "$returnSign${CurrencyHelper.format(metrics.returnMinorUnits, currency)} ($returnPercentStr)",
            returnPercent = metrics.returnPercent,
            isPositiveReturn = metrics.returnMinorUnits >= 0,
            dividendsFormatted = CurrencyHelper.format(metrics.dividendsTotalMinorUnits, currency),
            valueSnapshots = snapshots,
            entries = entryUiModels
        )
    }

    private fun entryTypeColor(type: EntryType): Long = when (type) {
        EntryType.CASH_INJECTION -> 0xFF039BE5L
        EntryType.VALUE_SNAPSHOT -> 0xFF33B679L
        EntryType.WITHDRAWAL    -> 0xFFE53935L
        EntryType.DIVIDEND      -> 0xFFF59300L
        EntryType.NOTE          -> 0xFF616161L
    }
}

fun computeMetrics(
    investment: InvestmentEntity,
    entriesSortedAsc: List<InvestmentEntryEntity>
): InvestmentMetrics {
    val cashInjections = entriesSortedAsc
        .filter { it.entryType == EntryType.CASH_INJECTION.storageKey }
        .sumOf { it.amountMinorUnits }

    val withdrawals = entriesSortedAsc
        .filter { it.entryType == EntryType.WITHDRAWAL.storageKey }
        .sumOf { it.amountMinorUnits }

    val totalInvested = cashInjections - withdrawals

    val latestSnapshot = entriesSortedAsc
        .filter { it.entryType == EntryType.VALUE_SNAPSHOT.storageKey }
        .lastOrNull()

    val today = LocalDate.now()
    val currentValue: Long = when {
        latestSnapshot != null -> {
            val withdrawalsAfterSnapshot = entriesSortedAsc
                .filter { it.entryType == EntryType.WITHDRAWAL.storageKey }
                .filter { it.date > latestSnapshot.date || (it.date == latestSnapshot.date && it.id > latestSnapshot.id) }
                .sumOf { it.amountMinorUnits }
            maxOf(0L, latestSnapshot.amountMinorUnits - withdrawalsAfterSnapshot)
        }
        investment.annualRatePercent != null && totalInvested > 0L -> {
            val firstInjection = entriesSortedAsc
                .filter { it.entryType == EntryType.CASH_INJECTION.storageKey }
                .minOfOrNull { LocalDate.parse(it.date) } ?: today
            val daysElapsed = ChronoUnit.DAYS.between(firstInjection, today).toDouble()
            val rate = investment.annualRatePercent / 100.0
            (totalInvested * (1.0 + rate).pow(daysElapsed / 365.25)).toLong()
        }
        else -> totalInvested
    }

    val returnAmount = currentValue - totalInvested
    val returnPercent: Float? = if (totalInvested > 0L)
        (returnAmount.toFloat() / totalInvested.toFloat()) * 100f
    else null

    val dividendsTotal = entriesSortedAsc
        .filter { it.entryType == EntryType.DIVIDEND.storageKey }
        .sumOf { it.amountMinorUnits }

    return InvestmentMetrics(
        totalInvestedMinorUnits = totalInvested,
        currentValueMinorUnits = currentValue,
        returnMinorUnits = returnAmount,
        returnPercent = returnPercent,
        dividendsTotalMinorUnits = dividendsTotal
    )
}
