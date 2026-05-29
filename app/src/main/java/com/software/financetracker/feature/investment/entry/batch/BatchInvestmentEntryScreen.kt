package com.software.financetracker.feature.investment.entry.batch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.software.financetracker.core.presentation.CopVisualTransformation
import com.software.financetracker.core.presentation.DecimalVisualTransformation
import com.software.financetracker.core.util.CurrencyHelper
import com.software.financetracker.domain.model.investment.EntryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchInvestmentEntryScreen(
    state: BatchInvestmentEntryState,
    onAction: (BatchInvestmentEntryAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar entradas") },
                navigationIcon = {
                    IconButton(onClick = { onAction(BatchInvestmentEntryAction.OnBackClick) }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { onAction(BatchInvestmentEntryAction.OnAddRow) }) {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Agregar fila")
                    }
                    Button(
                        onClick = { onAction(BatchInvestmentEntryAction.OnSaveAll) },
                        enabled = !state.isSaving,
                        shape = MaterialTheme.shapes.large
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Guardar (${state.rows.size})")
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(state.rows, key = { _, row -> row.rowId }) { index, row ->
                InvestmentEntryRowCard(
                    row = row,
                    index = index,
                    currency = state.investmentCurrency,
                    canRemove = state.rows.size > 1,
                    onAction = onAction
                )
            }
        }
    }

    val activePickerRow = state.rows.firstOrNull { it.showDatePicker }
    if (activePickerRow != null) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { onAction(BatchInvestmentEntryAction.OnDismissDatePicker) },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let {
                        onAction(BatchInvestmentEntryAction.OnDateSelected(activePickerRow.rowId, it))
                    }
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { onAction(BatchInvestmentEntryAction.OnDismissDatePicker) }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun InvestmentEntryRowCard(
    row: InvestmentEntryRowItem,
    index: Int,
    currency: String,
    canRemove: Boolean,
    onAction: (BatchInvestmentEntryAction) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Entrada ${index + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (canRemove) {
                    IconButton(
                        onClick = { onAction(BatchInvestmentEntryAction.OnRemoveRow(row.rowId)) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Eliminar fila", modifier = Modifier.size(18.dp))
                    }
                }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(EntryType.entries) { type ->
                    FilterChip(
                        selected = row.selectedType == type,
                        onClick = { onAction(BatchInvestmentEntryAction.OnTypeSelected(row.rowId, type)) },
                        label = { Text(type.labelEs) }
                    )
                }
            }

            AnimatedVisibility(
                visible = row.showAmountField,
                enter = expandVertically(animationSpec = tween(250), expandFrom = Alignment.Top) +
                        fadeIn(animationSpec = tween(200)),
                exit = shrinkVertically(animationSpec = tween(200), shrinkTowards = Alignment.Top) +
                        fadeOut(animationSpec = tween(150))
            ) {
                OutlinedTextField(
                    value = row.amountInput,
                    onValueChange = { onAction(BatchInvestmentEntryAction.OnAmountChange(row.rowId, it)) },
                    label = { Text("Monto") },
                    prefix = { Text(CurrencyHelper.currencySymbol(currency) + " ") },
                    isError = row.amountError != null,
                    supportingText = { row.amountError?.let { Text(it.asString()) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (currency == "COP") KeyboardType.Number else KeyboardType.Decimal
                    ),
                    visualTransformation = if (currency == "COP") CopVisualTransformation()
                    else DecimalVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = row.notes,
                onValueChange = { onAction(BatchInvestmentEntryAction.OnNotesChange(row.rowId, it)) },
                label = { Text("Notas (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(BatchInvestmentEntryAction.OnDateFieldClick(row.rowId)) }
            ) {
                OutlinedTextField(
                    value = row.displayDate.ifEmpty { "Seleccionar fecha" },
                    onValueChange = {},
                    label = { Text("Fecha") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Rounded.CalendarToday, contentDescription = "Seleccionar fecha")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = if (row.displayDate.isEmpty())
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
