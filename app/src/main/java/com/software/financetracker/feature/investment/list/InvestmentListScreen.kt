package com.software.financetracker.feature.investment.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.software.financetracker.core.util.CurrencyHelper
import com.software.financetracker.ui.components.iconForKey
import com.software.financetracker.ui.theme.Shapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentListScreen(
    state: InvestmentListState,
    onAction: (InvestmentListAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Inversiones") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(InvestmentListAction.OnAddClick) },
                shape = Shapes.medium
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Nueva inversión")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            state.investments.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(32.dp),
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
                            "Sin inversiones",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Agrega tu primera inversión para comenzar a rastrear tu portafolio.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(Modifier.height(4.dp))
                        Button(
                            onClick = { onAction(InvestmentListAction.OnAddClick) },
                            shape = Shapes.large
                        ) {
                            Text("Agregar inversión")
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.portfolioSummary?.let { summary ->
                        item { PortfolioSummaryCard(summary = summary) }
                    }
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

@Composable
private fun PortfolioSummaryCard(summary: PortfolioSummary) {
    val returnColor = if (summary.returnMinorUnits >= 0) Color(0xFF33B679) else MaterialTheme.colorScheme.error
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
                "PORTAFOLIO",
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Invertido", style = MaterialTheme.typography.bodyMedium, color = onContainer.copy(alpha = 0.7f))
                Text(
                    CurrencyHelper.format(summary.totalInvestedMinorUnits, "COP"),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = onContainer
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Retorno", style = MaterialTheme.typography.bodyMedium, color = onContainer.copy(alpha = 0.7f))
                val sign = if (summary.returnMinorUnits >= 0) "+" else ""
                val pctStr = summary.returnPercent?.let { " ($sign${String.format("%.1f", it)}%)" } ?: ""
                Text(
                    "$sign${CurrencyHelper.format(summary.returnMinorUnits, "COP")}$pctStr",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = returnColor
                )
            }
            if (!summary.isCopOnly) {
                Text(
                    "* solo inversiones en COP",
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
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
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
                Text(card.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
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
