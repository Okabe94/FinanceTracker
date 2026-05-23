package com.software.financetracker.feature.investment.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.software.financetracker.core.util.CurrencyHelper
import com.software.financetracker.ui.components.iconForKey
import com.software.financetracker.ui.theme.Shapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentDetailScreen(
    state: InvestmentDetailState,
    onAction: (InvestmentDetailAction) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.investmentName) },
                navigationIcon = {
                    IconButton(onClick = { onAction(InvestmentDetailAction.OnBackClick) }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { onAction(InvestmentDetailAction.OnEditClick) }) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Editar")
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Rounded.MoreVert, contentDescription = "Más opciones")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Guardar CSV") },
                                onClick = {
                                    showMenu = false
                                    onAction(InvestmentDetailAction.SaveEntries)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Compartir CSV") },
                                onClick = {
                                    showMenu = false
                                    onAction(InvestmentDetailAction.ShareEntries)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar inversión") },
                                onClick = {
                                    showMenu = false
                                    onAction(InvestmentDetailAction.OnDeleteInvestmentClick)
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(InvestmentDetailAction.OnAddEntryClick) },
                shape = Shapes.medium
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Agregar movimiento")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = Shapes.medium) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(state.colorArgb)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = iconForKey(state.iconKey),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = state.currentValueFormatted,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Valor actual · ${state.currency}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        MetricRow("Invertido", state.totalInvestedFormatted)
                        MetricRow(
                            label = "Retorno",
                            value = state.returnFormatted,
                            valueColor = if (state.isPositiveReturn)
                                Color(0xFF33B679) else MaterialTheme.colorScheme.error
                        )
                        if (state.dividendsFormatted.isNotEmpty()) {
                            MetricRow("Dividendos", state.dividendsFormatted)
                        }
                    }
                }
            }

            if (state.annualRatePercent != null) {
                item {
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = Shapes.medium) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Tasa fija anual", style = MaterialTheme.typography.labelLarge)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${String.format("%.2f", state.annualRatePercent)}% anual",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            state.maturityDateDisplay?.let {
                                Text(
                                    "Vence: $it",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (state.valueSnapshots.size >= 2) {
                item {
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = Shapes.medium) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Historial de valor", style = MaterialTheme.typography.labelLarge)
                            Spacer(Modifier.height(12.dp))
                            ValueLineChart(
                                snapshots = state.valueSnapshots,
                                currency = state.currency
                            )
                        }
                    }
                }
            }

            if (state.entries.isNotEmpty()) {
                item {
                    Text(
                        "Movimientos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                items(state.entries, key = { it.id }) { entry ->
                    val density = LocalDensity.current
                    val positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold
                    val dismissState = remember {
                        SwipeToDismissBoxState(
                            initialValue = SwipeToDismissBoxValue.Settled,
                            density = density,
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    onAction(InvestmentDetailAction.DeleteEntrySwipe(entry.id))
                                }
                                value == SwipeToDismissBoxValue.EndToStart
                            },
                            positionalThreshold = positionalThreshold
                        )
                    }
                    SwipeToDismissBox(
                        state = dismissState,
                        modifier = Modifier.animateItem(),
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(Shapes.medium)
                                    .background(MaterialTheme.colorScheme.errorContainer)
                                    .padding(end = 16.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Rounded.Delete,
                                    contentDescription = "Eliminar movimiento",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    ) {
                        EntryRow(
                            entry = entry,
                            onClick = { onAction(InvestmentDetailAction.OnEntryClick(entry.id)) }
                        )
                    }
                }
                item { Spacer(Modifier.height(64.dp)) }
            }
        }
    }

    if (state.showDeleteInvestmentDialog) {
        AlertDialog(
            onDismissRequest = { onAction(InvestmentDetailAction.OnDeleteInvestmentDismiss) },
            title = { Text("Eliminar inversión") },
            text = { Text("¿Estás seguro? Se eliminarán todos los movimientos de esta inversión.") },
            confirmButton = {
                Button(
                    onClick = { onAction(InvestmentDetailAction.OnDeleteInvestmentConfirm) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { onAction(InvestmentDetailAction.OnDeleteInvestmentDismiss) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = valueColor)
    }
}

@Composable
private fun EntryRow(entry: EntryUiModel, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = Shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(entry.typeColor))
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        entry.typeLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        entry.dateDisplay,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                entry.amountFormatted?.let {
                    Text(it, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                }
                if (entry.notes.isNotBlank()) {
                    Text(
                        entry.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun ValueLineChart(snapshots: List<SnapshotPoint>, currency: String) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(snapshots) {
        modelProducer.runTransaction {
            lineSeries { series(snapshots.map { it.amountMinorUnits.toFloat() }) }
        }
    }
    val xFormatter = CartesianValueFormatter { _, x, _ ->
        snapshots.getOrNull(x.toInt())?.dateLabel ?: ""
    }
    val yFormatter = CartesianValueFormatter { _, y, _ ->
        CurrencyHelper.formatAbbr(y.toLong(), currency)
    }
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(valueFormatter = yFormatter),
            bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = xFormatter)
        ),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    )
}
