package com.software.financetracker.feature.income.recurring.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.software.financetracker.domain.model.displayName
import com.software.financetracker.ui.theme.Shapes
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringIncomeListScreen(
    state: RecurringIncomeListState,
    onAction: (RecurringIncomeListAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ingresos recurrentes") },
                navigationIcon = {
                    IconButton(onClick = { onAction(RecurringIncomeListAction.OnBackClick) }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(RecurringIncomeListAction.OnAddClick) }) {
                Icon(Icons.Rounded.Add, contentDescription = "Nuevo recurrente")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        RecurringIncomeListContent(state, onAction, Modifier.padding(innerPadding))
    }
}

@Composable
internal fun RecurringIncomeListContent(
    state: RecurringIncomeListState,
    onAction: (RecurringIncomeListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        state.isLoading -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        state.templates.isEmpty() -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AttachMoney,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text("Sin ingresos recurrentes", style = MaterialTheme.typography.titleMedium)
                Text("Toca + para agregar tu salario u otros ingresos fijos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.templates, key = { it.id }) { template ->
                RecurringIncomeItem(
                    template = template,
                    onClick = { onAction(RecurringIncomeListAction.OnTemplateClick(template.id)) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
private fun RecurringIncomeItem(
    template: RecurringIncomeTemplateUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(onClick = onClick, shape = Shapes.medium, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(template.source, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                        Text(
                            text = template.recurrenceType.displayName(),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Text(
                    text = "Próximo: ${template.displayNextDueDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatCop(template.amountCop),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun formatCop(amount: Long): String =
    "$ " + NumberFormat.getNumberInstance(Locale("es", "CO")).format(amount)
