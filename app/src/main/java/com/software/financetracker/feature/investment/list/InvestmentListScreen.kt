package com.software.financetracker.feature.investment.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.software.financetracker.R
import com.software.financetracker.core.util.CurrencyHelper
import com.software.financetracker.ui.components.DonutChart
import com.software.financetracker.ui.components.DonutLegend
import com.software.financetracker.ui.components.iconForKey
import com.software.financetracker.ui.theme.Shapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentListScreen(
    state: InvestmentListState,
    onAction: (InvestmentListAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.investment_list_title)) },
                actions = {
                    IconButton(onClick = { onAction(InvestmentListAction.OnRatesBottomSheetToggled) }) {
                        Icon(
                            Icons.Rounded.Tune,
                            contentDescription = stringResource(R.string.investment_list_rates_cd)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(InvestmentListAction.OnAddClick) },
                shape = Shapes.medium
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.investment_list_new_cd)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (state.showRatesBottomSheet) {
            ExchangeRatesBottomSheet(
                rates = state.rates,
                ratesUpdatedAt = state.ratesUpdatedAt,
                isRefreshing = state.isRefreshingRates,
                onDismiss = { onAction(InvestmentListAction.OnRatesBottomSheetToggled) },
                onRefresh = { onAction(InvestmentListAction.RefreshRates) }
            )
        }

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            state.totalCount == 0 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            stringResource(R.string.investment_list_empty_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            stringResource(R.string.investment_list_empty_message),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(Modifier.height(4.dp))
                        Button(
                            onClick = { onAction(InvestmentListAction.OnAddClick) },
                            shape = Shapes.large
                        ) {
                            Text(stringResource(R.string.investment_list_add_button))
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 80.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Portfolio summary
                    state.portfolioSummary?.let { summary ->
                        item { PortfolioSummaryCard(summary = summary) }
                    }

                    // 2. Allocation donut (visible when ≥2 investments have value)
                    if (state.allocationSlices.size >= 2) {
                        item {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = Shapes.medium
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        stringResource(R.string.investment_list_distribution),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    DonutChart(
                                        slices = state.allocationSlices,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                    )
                                    DonutLegend(
                                        slices = state.allocationSlices,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    // 3. Search and currency filters
                    item {
                        SearchAndFilterRow(
                            searchQuery = state.searchQuery,
                            availableCurrencies = state.availableCurrencies,
                            activeCurrencyFilter = state.activeCurrencyFilter,
                            onAction = onAction
                        )
                    }

                    // 4. Investment cards (or empty-search state)
                    if (state.investments.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        stringResource(R.string.investment_list_empty_search),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(state.investments, key = { it.id }) { card ->
                            InvestmentCard(
                                card = card,
                                onClick = { onAction(InvestmentListAction.OnCardClick(card.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchAndFilterRow(
    searchQuery: String,
    availableCurrencies: List<String>,
    activeCurrencyFilter: String?,
    onAction: (InvestmentListAction) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { onAction(InvestmentListAction.OnSearchQueryChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.investment_list_search_placeholder)) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            singleLine = true,
            shape = Shapes.medium
        )
        if (availableCurrencies.size > 1) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableCurrencies.forEach { currency ->
                    FilterChip(
                        selected = activeCurrencyFilter == currency,
                        onClick = {
                            val newFilter = if (activeCurrencyFilter == currency) null else currency
                            onAction(InvestmentListAction.OnCurrencyFilterChanged(newFilter))
                        },
                        label = { Text(currency) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExchangeRatesBottomSheet(
    rates: Map<String, Double>,
    ratesUpdatedAt: String?,
    isRefreshing: Boolean,
    onDismiss: () -> Unit,
    onRefresh: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.investment_list_rates_title),
                    style = MaterialTheme.typography.titleMedium
                )
                if (isRefreshing) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    TextButton(onClick = onRefresh) { Text(stringResource(R.string.action_update)) }
                }
            }
            val displayPairs = listOf("USD", "EUR", "GBP")
            displayPairs.forEach { currency ->
                val rate = rates[currency]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "1 $currency",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        if (rate != null) "COP ${String.format("%,.0f", rate)}" else "–",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    )
                }
            }
            ratesUpdatedAt?.let {
                Text(
                    stringResource(R.string.investment_list_rates_updated_at, it),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } ?: Text(
                stringResource(R.string.investment_list_rates_no_data),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PortfolioSummaryCard(summary: PortfolioSummary) {
    val returnColor =
        if (summary.returnMinorUnits >= 0) Color(0xFF33B679) else MaterialTheme.colorScheme.error
    val onContainer = MaterialTheme.colorScheme.onPrimaryContainer
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                stringResource(R.string.investment_list_portfolio),
                style = MaterialTheme.typography.labelMedium,
                color = onContainer.copy(alpha = 0.7f)
            )
            Text(
                CurrencyHelper.format(summary.totalValueMinorUnits, "COP"),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = onContainer
            )
            HorizontalDivider(color = onContainer.copy(alpha = 0.12f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.investment_list_invested_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = onContainer.copy(alpha = 0.7f)
                )
                Text(
                    CurrencyHelper.format(summary.totalInvestedMinorUnits, "COP"),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = onContainer
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.investment_list_return_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = onContainer.copy(alpha = 0.7f)
                )
                val sign = if (summary.returnMinorUnits >= 0) "+" else ""
                val pctStr =
                    summary.returnPercent?.let { " ($sign${String.format("%.1f", it)}%)" } ?: ""
                Text(
                    "$sign${CurrencyHelper.format(summary.returnMinorUnits, "COP")}$pctStr",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = returnColor
                )
            }
            if (!summary.isCopOnly) {
                Text(
                    stringResource(R.string.investment_list_cop_only_note),
                    style = MaterialTheme.typography.labelSmall,
                    color = onContainer.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun InvestmentCard(card: InvestmentCardUiModel, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = Shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(card.colorArgb)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconForKey(card.iconKey),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    card.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    card.currency,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    card.currentValueFormatted,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                card.returnPercent?.let { pct ->
                    val sign = if (card.isPositiveReturn) "+" else ""
                    val color = if (card.isPositiveReturn) Color(0xFF33B679)
                    else MaterialTheme.colorScheme.error
                    Surface(
                        shape = CircleShape,
                        color = color.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "$sign${String.format("%.1f", pct)}%",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } ?: Text(
                    "–",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
