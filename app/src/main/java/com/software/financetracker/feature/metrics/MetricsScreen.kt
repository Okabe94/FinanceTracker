package com.software.financetracker.feature.metrics

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.stacked
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.software.financetracker.ui.theme.Shapes
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricsScreen(
    state: MetricsState,
    onAction: (MetricsAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Métricas") },
                navigationIcon = {
                    IconButton(onClick = { onAction(MetricsAction.OnBackClick) }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    var showMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Rounded.FileDownload, contentDescription = "Exportar")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Guardar en Descargas") },
                                leadingIcon = { Icon(Icons.Rounded.Save, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    onAction(MetricsAction.OnSaveClick)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Compartir") },
                                leadingIcon = { Icon(Icons.Rounded.Share, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    onAction(MetricsAction.OnShareClick)
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = state.isLoading,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 300)) togetherWith
                    fadeOut(animationSpec = tween(durationMillis = 150))
            },
            label = "metricsContent"
        ) { isLoading ->
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // Current month summary
                    item { CurrentMonthKpiCard(state) }
                    item { BurnRateCard(state) }
                    if (state.dailyAvgCop > 0L) {
                        item { ProyeccionCard(state) }
                    }

                    // Quick stats
                    item { SectionHeader("Variación mensual") }
                    item { MomDeltaCard(state.momDeltaPercent, state.momDeltaLabel) }
                    item { SectionHeader("Promedio mensual") }
                    item { AverageSpendCard(state.averageMonthlySpend) }

                    if (state.topCategoryName.isNotEmpty()) {
                        item { SectionHeader("Categoría principal") }
                        item { TopCategoryCard(state) }
                    }

                    if (state.topExpenses.isNotEmpty()) {
                        item { SectionHeader("Mayores gastos del mes") }
                        item { TopExpensesCard(state.topExpenses) }
                    }

                    if (state.bestMonthLabel.isNotEmpty()) {
                        item { MejorPeorMesCard(state) }
                    }

                    // Range-based charts
                    item { RangeChipRow(state.selectedRange) { onAction(MetricsAction.OnRangeSelected(it)) } }
                    item { SectionHeader("Tendencia de gasto") }
                    item { MonthlyTotalsBarChart(state.monthlyTotals) }

                    item { SectionHeader("Por categoría") }
                    if (state.allCategories.isNotEmpty()) {
                        item {
                            CategoryFilterChips(
                                categories = state.allCategories,
                                selectedCategoryId = state.selectedCategoryId,
                                onAction = onAction
                            )
                        }
                    }
                    item {
                        val displayedTrend = if (state.selectedCategoryId != null)
                            state.categoryTrend.map { m ->
                                m.copy(slices = m.slices.filter { it.categoryId == state.selectedCategoryId })
                            }
                        else state.categoryTrend
                        val displayedCats = if (state.selectedCategoryId != null)
                            state.allCategories.filter { it.categoryId == state.selectedCategoryId }
                        else state.allCategories
                        CategoryStackedBarChart(displayedTrend, displayedCats)
                    }
                    if (state.allCategories.isNotEmpty() && state.selectedCategoryId == null) {
                        item { CategoryLegend(state.allCategories) }
                    }

                    if (state.spendByDayOfWeek.any { it.totalCop > 0L }) {
                        item { SectionHeader("Gasto por día de la semana") }
                        item { DayOfWeekBarChart(state.spendByDayOfWeek) }
                    }

                    if (state.overLimitByMonth.isNotEmpty()) {
                        item { SectionHeader("Categorías sobre su límite") }
                        state.overLimitByMonth.forEach { month ->
                            item { OverLimitMonthCard(month) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentMonthKpiCard(state: MetricsState) {
    val isOver = state.currentMonthHasLimit && state.currentMonthTotalCop > state.currentMonthLimitCop
    ElevatedCard(
        shape = Shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(state.currentMonthLabel, style = MaterialTheme.typography.titleMedium)
            Text(
                text = formatCop(state.currentMonthTotalCop),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            if (state.currentMonthHasLimit && state.currentMonthLimitCop > 0L) {
                val pct = (state.currentMonthTotalCop.toFloat() / state.currentMonthLimitCop * 100).toInt()
                LinearProgressIndicator(
                    progress = { (state.currentMonthTotalCop.toFloat() / state.currentMonthLimitCop).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isOver) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$pct% del presupuesto",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isOver) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (state.currentMonthOverLimitCount > 0) {
                        Text(
                            text = "${state.currentMonthOverLimitCount} sobre límite",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BurnRateCard(state: MetricsState) {
    ElevatedCard(
        shape = Shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${state.daysLeftInMonth}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "días restantes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp)
                    .align(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatCop(state.dailyAvgCop),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "promedio diario",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (state.currentMonthHasLimit) {
                HorizontalDivider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                val remaining = state.currentMonthLimitCop - state.currentMonthTotalCop
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatCop(remaining),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (remaining < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "presupuesto restante",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun TopCategoryCard(state: MetricsState) {
    ElevatedCard(
        shape = Shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(state.topCategoryColorArgb).copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(state.topCategoryColorArgb))
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.topCategoryName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${state.topCategorySharePercent}% del total del mes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatCop(state.topCategoryTotalCop),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TopExpensesCard(expenses: List<TopExpenseUiModel>) {
    ElevatedCard(
        shape = Shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            expenses.forEachIndexed { index, expense ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = expense.description,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(expense.colorArgb))
                            )
                            Text(
                                text = expense.categoryName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "·",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = expense.dateLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = formatCop(expense.amountCop),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterChips(
    categories: List<CategorySliceUiModel>,
    selectedCategoryId: Long?,
    onAction: (MetricsAction) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategoryId == null,
                onClick = { onAction(MetricsAction.OnCategorySelected(null)) },
                label = { Text("Todas") }
            )
        }
        items(categories) { cat ->
            FilterChip(
                selected = cat.categoryId == selectedCategoryId,
                onClick = {
                    onAction(
                        MetricsAction.OnCategorySelected(
                            if (cat.categoryId == selectedCategoryId) null else cat.categoryId
                        )
                    )
                },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(cat.colorArgb))
                    )
                },
                label = { Text(cat.name) }
            )
        }
    }
}

@Composable
private fun DayOfWeekBarChart(data: List<DayOfWeekUiModel>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(data) {
        modelProducer.runTransaction {
            columnSeries { series(data.map { it.totalCop.toFloat() }) }
        }
    }
    val labels = data.map { it.dayLabel }
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = CartesianValueFormatter { _, value, _ ->
                    formatAbbrCop(value.toLong())
                }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = CartesianValueFormatter { _, value, _ ->
                    labels.getOrElse(value.toInt()) { "" }
                }
            )
        ),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 8.dp)
    )
}

@Composable
private fun OverLimitMonthCard(month: OverLimitMonthUiModel) {
    ElevatedCard(
        shape = Shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = month.monthLabel,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            month.categories.forEach { cat ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(cat.colorArgb))
                    )
                    Text(
                        text = cat.categoryName,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = formatCop(cat.spentCop),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Text(
                            text = "límite: ${formatCop(cat.limitCop)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProyeccionCard(state: MetricsState) {
    val isOverProjected = state.currentMonthHasLimit && state.projectedMonthCop > state.currentMonthLimitCop
    ElevatedCard(
        shape = Shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Proyección del mes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatCop(state.projectedMonthCop),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (isOverProjected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "si mantienes el ritmo actual",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (state.currentMonthHasLimit && state.currentMonthLimitCop > 0L) {
                val projPct = (state.projectedMonthCop.toFloat() / state.currentMonthLimitCop).coerceIn(0f, 1.5f)
                LinearProgressIndicator(
                    progress = { projPct.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isOverProjected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                if (isOverProjected) {
                    Text(
                        text = "Podrías superar el presupuesto por ${formatCop(state.projectedMonthCop - state.currentMonthLimitCop)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun MejorPeorMesCard(state: MetricsState) {
    ElevatedCard(
        shape = Shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Mejor mes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = state.bestMonthLabel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = formatCop(state.bestMonthTotalCop),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .height(52.dp)
                    .width(1.dp)
                    .align(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Peor mes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = state.worstMonthLabel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = formatCop(state.worstMonthTotalCop),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun RangeChipRow(selectedRange: TrendRange, onRangeSelected: (TrendRange) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TrendRange.entries.forEach { range ->
            FilterChip(
                selected = range == selectedRange,
                onClick = { onRangeSelected(range) },
                label = { Text(range.labelEs) }
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun MonthlyTotalsBarChart(data: List<MonthlyTotalUiModel>) {
    if (data.isEmpty()) {
        ChartEmptyPlaceholder()
        return
    }
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(data) {
        modelProducer.runTransaction {
            columnSeries { series(data.map { it.totalCop.toFloat() }) }
        }
    }
    val labels = data.map { it.label }
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = CartesianValueFormatter { _, value, _ ->
                    formatAbbrCop(value.toLong())
                }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = CartesianValueFormatter { _, value, _ ->
                    labels.getOrElse(value.toInt()) { "" }
                }
            )
        ),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 8.dp)
    )
}

@Composable
private fun CategoryStackedBarChart(
    trend: List<CategoryTrendUiModel>,
    allCategories: List<CategorySliceUiModel>
) {
    if (trend.isEmpty() || allCategories.isEmpty()) {
        ChartEmptyPlaceholder()
        return
    }
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(trend, allCategories) {
        modelProducer.runTransaction {
            columnSeries {
                allCategories.forEach { cat ->
                    series(trend.map { month ->
                        month.slices.find { it.categoryId == cat.categoryId }?.totalCop?.toFloat() ?: 0f
                    })
                }
            }
        }
    }
    val columns = remember(allCategories) {
        allCategories.map { cat -> LineComponent(Fill(cat.colorArgb), 8f) }
    }
    val labels = trend.map { it.label }
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(columns),
                mergeMode = { ColumnCartesianLayer.MergeMode.stacked() }
            ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = CartesianValueFormatter { _, value, _ ->
                    formatAbbrCop(value.toLong())
                }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = CartesianValueFormatter { _, value, _ ->
                    labels.getOrElse(value.toInt()) { "" }
                }
            )
        ),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(horizontal = 8.dp)
    )
}

@Composable
private fun CategoryLegend(categories: List<CategorySliceUiModel>) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        categories.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { cat ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(cat.colorArgb))
                        )
                        Text(
                            text = cat.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MomDeltaCard(momDeltaPercent: Float?, momDeltaLabel: String) {
    ElevatedCard(
        shape = Shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        if (momDeltaPercent == null) {
            Text(
                text = "Datos insuficientes para comparar meses",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            val isPositive = momDeltaPercent >= 0f
            val deltaColor = if (isPositive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = if (isPositive) Icons.Rounded.TrendingUp else Icons.Rounded.TrendingDown,
                    contentDescription = null,
                    tint = deltaColor,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = momDeltaLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = deltaColor
                    )
                    Text(
                        text = if (isPositive) "Gastaste más que el mes pasado"
                               else "Gastaste menos que el mes pasado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AverageSpendCard(average: Long) {
    ElevatedCard(
        shape = Shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = formatCop(average),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "promedio mensual en el período",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ChartEmptyPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Sin datos para el período seleccionado",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatCop(amount: Long): String =
    "$ " + NumberFormat.getNumberInstance(Locale("es", "CO")).format(amount)

private fun formatAbbrCop(amount: Long): String = when {
    amount >= 1_000_000L -> String.format("%.1fM", amount / 1_000_000.0)
    amount >= 1_000L -> String.format("%.0fK", amount / 1_000.0)
    else -> amount.toString()
}
