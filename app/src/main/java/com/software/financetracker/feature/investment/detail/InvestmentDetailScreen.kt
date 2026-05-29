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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.dashed
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.software.financetracker.R
import com.software.financetracker.core.util.CurrencyHelper
import com.software.financetracker.ui.components.iconForKey
import com.software.financetracker.ui.theme.GreenAccent
import com.software.financetracker.ui.theme.Shapes

private val benchmarkRates = listOf(
    null,
    8.0,
    10.0,
    12.0,
    -1.0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentDetailScreen(
    state: InvestmentDetailState,
    onAction: (InvestmentDetailAction) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.investmentName) },
                navigationIcon = {
                    IconButton(onClick = { onAction(InvestmentDetailAction.OnBackClick) }) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onAction(InvestmentDetailAction.OnEditClick) }) {
                        Icon(
                            Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.investment_detail_edit_cd)
                        )
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Icons.Rounded.MoreVert,
                                contentDescription = stringResource(R.string.investment_detail_more_cd)
                            )
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Agregar múltiples entradas") },
                                onClick = {
                                    showMenu = false
                                    onAction(InvestmentDetailAction.OnBatchAddEntryClick)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.investment_detail_save_csv)) },
                                onClick = {
                                    showMenu = false
                                    onAction(InvestmentDetailAction.SaveEntries)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.investment_detail_share_csv)) },
                                onClick = {
                                    showMenu = false
                                    onAction(InvestmentDetailAction.ShareEntries)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.investment_detail_delete_menu_item)) },
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
                shape = Shapes.medium,
                containerColor = GreenAccent,
                contentColor = Color.White
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.investment_detail_add_entry_cd)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 80.dp
            ),
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
                                    text = stringResource(
                                        R.string.investment_detail_current_value,
                                        state.currency
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        MetricRow(
                            stringResource(R.string.investment_detail_invested_label),
                            state.totalInvestedFormatted
                        )
                        MetricRow(
                            label = stringResource(R.string.investment_detail_return_label),
                            value = state.returnFormatted,
                            valueColor = if (state.isPositiveReturn)
                                Color(0xFF33B679) else MaterialTheme.colorScheme.error
                        )
                        if (state.dividendsFormatted.isNotEmpty()) {
                            MetricRow(
                                stringResource(R.string.investment_detail_dividends_label),
                                state.dividendsFormatted
                            )
                        }
                    }
                }
            }

            if (state.annualRatePercent != null) {
                item {
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = Shapes.medium) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                stringResource(R.string.investment_detail_fixed_rate_label),
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${String.format("%.2f", state.annualRatePercent)}% anual",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            state.maturityDateDisplay?.let {
                                Text(
                                    stringResource(R.string.investment_detail_expires, it),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (state.targetValueMinorUnits != null && state.targetProgress != null) {
                item {
                    GoalProgressCard(
                        currentValueFormatted = state.currentValueFormatted,
                        targetValueFormatted = state.targetValueFormatted ?: "",
                        targetDateDisplay = state.targetDateDisplay,
                        progress = state.targetProgress
                    )
                }
            }

            if (state.valueSnapshots.size >= 2) {
                item {
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = Shapes.medium) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    stringResource(R.string.investment_detail_history_label),
                                    style = MaterialTheme.typography.labelLarge
                                )
                                BenchmarkPickerButton(
                                    currentRate = state.benchmarkRatePercent,
                                    onAction = onAction
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            ValueLineChart(
                                snapshots = state.valueSnapshots,
                                currency = state.currency,
                                benchmarkData = state.benchmarkChartData
                            )
                            if (state.benchmarkChartData.isNotEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                BenchmarkLegend(
                                    investmentName = state.investmentName,
                                    benchmarkRatePercent = state.benchmarkRatePercent ?: 0.0
                                )
                            }
                        }
                    }
                }
            }

            if (state.entries.isNotEmpty()) {
                item {
                    Text(
                        stringResource(R.string.investment_detail_movements_label),
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
                                    contentDescription = stringResource(R.string.investment_detail_delete_entry_cd),
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
            title = { Text(stringResource(R.string.investment_detail_delete_title)) },
            text = { Text(stringResource(R.string.investment_detail_delete_message)) },
            confirmButton = {
                Button(
                    onClick = { onAction(InvestmentDetailAction.OnDeleteInvestmentConfirm) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { onAction(InvestmentDetailAction.OnDeleteInvestmentDismiss) }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun BenchmarkPickerButton(
    currentRate: Double?,
    onAction: (InvestmentDetailAction) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var customRateInput by remember { mutableStateOf("") }

    val noComparison = stringResource(R.string.investment_detail_benchmark_no_comparison)
    val customLabel = stringResource(R.string.label_custom)
    val benchmarkLabels = listOf(noComparison, "CDT 8%", "Inflación 10%", "CDT 12%", customLabel)

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(
                text = if (currentRate == null) stringResource(R.string.investment_detail_benchmark_compare)
                else stringResource(
                    R.string.investment_detail_benchmark_ref_format,
                    when (currentRate) {
                        8.0 -> "CDT 8%"
                        10.0 -> "Inflación 10%"
                        12.0 -> "CDT 12%"
                        else -> String.format("%.0f%%", currentRate)
                    }
                ),
                style = MaterialTheme.typography.labelMedium
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            benchmarkRates.forEachIndexed { index, rate ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = benchmarkLabels[index],
                            fontWeight = if (rate == currentRate) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        expanded = false
                        when (rate) {
                            null -> onAction(InvestmentDetailAction.OnBenchmarkRateChanged(null))
                            -1.0 -> {
                                customRateInput =
                                    currentRate?.let { String.format("%.0f", it) } ?: ""
                                showCustomDialog = true
                            }

                            else -> onAction(InvestmentDetailAction.OnBenchmarkRateChanged(rate))
                        }
                    }
                )
            }
        }
    }

    if (showCustomDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            title = { Text(stringResource(R.string.investment_detail_benchmark_custom_title)) },
            text = {
                OutlinedTextField(
                    value = customRateInput,
                    onValueChange = {
                        customRateInput = it.filter { c -> c.isDigit() || c == '.' }
                    },
                    label = { Text(stringResource(R.string.investment_detail_benchmark_rate_label)) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val parsed = customRateInput.toDoubleOrNull()
                    if (parsed != null && parsed > 0.0) {
                        onAction(InvestmentDetailAction.OnBenchmarkRateChanged(parsed))
                    }
                    showCustomDialog = false
                }) { Text(stringResource(R.string.investment_detail_benchmark_apply)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCustomDialog = false
                }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}

@Composable
private fun BenchmarkLegend(investmentName: String, benchmarkRatePercent: Double) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val benchmarkColor = MaterialTheme.colorScheme.tertiary
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(primaryColor)
            )
            Spacer(Modifier.width(4.dp))
            Text(investmentName, style = MaterialTheme.typography.labelSmall)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(benchmarkColor)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                stringResource(
                    R.string.investment_detail_reference_legend,
                    String.format("%.0f%%", benchmarkRatePercent)
                ),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

@Composable
private fun EntryRow(entry: EntryUiModel, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
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
private fun GoalProgressCard(
    currentValueFormatted: String,
    targetValueFormatted: String,
    targetDateDisplay: String?,
    progress: Float,
) {
    val isReached = progress >= 1f
    val progressColor = if (isReached) Color(0xFF33B679) else MaterialTheme.colorScheme.primary
    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = Shapes.medium) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                stringResource(R.string.investment_detail_goal_section),
                style = MaterialTheme.typography.labelLarge
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = progressColor,
                trackColor = progressColor.copy(alpha = 0.2f)
            )
            if (isReached) {
                Text(
                    stringResource(R.string.investment_detail_goal_reached),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF33B679),
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                MetricRow(
                    stringResource(R.string.investment_detail_goal_actual_label),
                    currentValueFormatted
                )
                MetricRow(
                    stringResource(R.string.investment_detail_goal_target_label),
                    targetValueFormatted
                )
                targetDateDisplay?.let {
                    MetricRow(
                        stringResource(R.string.investment_detail_goal_date_label),
                        it
                    )
                }
                Text(
                    "${String.format("%.0f%%", progress * 100f)} alcanzado",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ValueLineChart(
    snapshots: List<SnapshotPoint>,
    currency: String,
    benchmarkData: List<Float> = emptyList(),
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(snapshots, benchmarkData) {
        val hasBenchmark = benchmarkData.isNotEmpty() && benchmarkData.size == snapshots.size
        modelProducer.runTransaction {
            lineSeries {
                series(snapshots.map { it.amountMinorUnits.toFloat() })
                if (hasBenchmark) series(benchmarkData)
            }
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val benchmarkColor = MaterialTheme.colorScheme.tertiary

    // Stable line specs — remember so the layer isn't recreated every recomposition
    val investmentLine = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(fill(primaryColor))
    )
    val benchmarkLine = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(fill(benchmarkColor)),
        stroke = LineCartesianLayer.LineStroke.dashed()
    )
    val lineProvider = remember(investmentLine, benchmarkLine) {
        LineCartesianLayer.LineProvider.series(investmentLine, benchmarkLine)
    }

    val xFormatter = CartesianValueFormatter { _, x, _ ->
        snapshots.getOrNull(x.toInt())?.dateLabel ?: ""
    }
    val yFormatter = CartesianValueFormatter { _, y, _ ->
        CurrencyHelper.formatAbbr(y.toLong(), currency)
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(lineProvider = lineProvider),
            startAxis = VerticalAxis.rememberStart(valueFormatter = yFormatter),
            bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = xFormatter)
        ),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    )
}
