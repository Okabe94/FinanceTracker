package com.software.financetracker.feature.income.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.software.financetracker.ui.theme.Shapes
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeListScreen(
    state: IncomeListState,
    onAction: (IncomeListAction) -> Unit
) {
    var showAddSheet by remember { mutableStateOf(false) }

    if (showAddSheet) {
        ModalBottomSheet(onDismissRequest = { showAddSheet = false }) {
            ListItem(
                headlineContent = { Text("Ingreso puntual") },
                supportingContent = { Text("Un pago o ingreso no periódico") },
                leadingContent = { Icon(Icons.Rounded.AttachMoney, contentDescription = null) },
                modifier = Modifier.clickable {
                    showAddSheet = false
                    onAction(IncomeListAction.OnAddIncomeClick)
                }
            )
            ListItem(
                headlineContent = { Text("Ingreso recurrente") },
                supportingContent = { Text("Salario u otros ingresos fijos") },
                leadingContent = { Icon(Icons.Rounded.Repeat, contentDescription = null) },
                modifier = Modifier.clickable {
                    showAddSheet = false
                    onAction(IncomeListAction.OnAddTemplateClick)
                }
            )
            Spacer(Modifier.height(16.dp))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ingresos") },
                navigationIcon = {
                    IconButton(onClick = { onAction(IncomeListAction.OnBackClick) }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }) {
                Icon(Icons.Rounded.Add, contentDescription = "Agregar ingreso")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.items.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
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
                    Text("Sin ingresos registrados", style = MaterialTheme.typography.titleMedium)
                    Text("Toca + para agregar uno", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = state.items,
                    key = { item ->
                        when (item) {
                            is IncomeItem.Entry -> "entry_${item.id}"
                            is IncomeItem.Template -> "template_${item.id}"
                        }
                    }
                ) { item ->
                    when (item) {
                        is IncomeItem.Entry -> IncomeEntryItem(
                            item = item,
                            onClick = { onAction(IncomeListAction.OnEntryClick(item.id)) },
                            modifier = Modifier.animateItem()
                        )
                        is IncomeItem.Template -> IncomeTemplateItem(
                            item = item,
                            onClick = { onAction(IncomeListAction.OnTemplateClick(item.id)) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IncomeEntryItem(
    item: IncomeItem.Entry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(onClick = onClick, shape = Shapes.medium, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.source, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text(item.displayDate, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (item.notes.isNotBlank()) {
                    Text(item.notes, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (item.isFromTemplate) {
                    Spacer(Modifier.height(4.dp))
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Recurrente", style = MaterialTheme.typography.labelSmall) },
                        icon = { Icon(Icons.Rounded.Repeat, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
            Text(
                text = formatCop(item.amountCop),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun IncomeTemplateItem(
    item: IncomeItem.Template,
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
                    Text(item.source, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                        Text(
                            text = item.recurrenceLabel,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Text(
                    "Próximo: ${item.displayNextDueDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatCop(item.amountCop),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun formatCop(amount: Long): String =
    "$ " + NumberFormat.getNumberInstance(Locale("es", "CO")).format(amount)
