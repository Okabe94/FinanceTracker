package com.software.financetracker.feature.investment.entry

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.software.financetracker.core.util.CurrencyHelper
import com.software.financetracker.domain.model.investment.EntryType
import com.software.financetracker.ui.theme.Shapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentEntryFormScreen(
    state: InvestmentEntryFormState,
    onAction: (InvestmentEntryFormAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.entryId == null) "Nuevo movimiento" else "Editar movimiento") },
                navigationIcon = {
                    IconButton(onClick = { onAction(InvestmentEntryFormAction.OnBackClick) }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (state.entryId != null) {
                        IconButton(onClick = { onAction(InvestmentEntryFormAction.OnDeleteClick) }) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            Text("Tipo de movimiento", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(EntryType.entries) { type ->
                    FilterChip(
                        selected = state.selectedType == type,
                        onClick = { onAction(InvestmentEntryFormAction.OnTypeSelected(type)) },
                        label = { Text(type.labelEs) }
                    )
                }
            }

            AnimatedVisibility(
                visible = state.showAmountField,
                enter = expandVertically(animationSpec = tween(250), expandFrom = Alignment.Top) +
                    fadeIn(animationSpec = tween(200)),
                exit = shrinkVertically(animationSpec = tween(200), shrinkTowards = Alignment.Top) +
                    fadeOut(animationSpec = tween(150))
            ) {
                OutlinedTextField(
                    value = state.amountInput,
                    onValueChange = { onAction(InvestmentEntryFormAction.OnAmountChange(it)) },
                    label = { Text("Monto") },
                    prefix = { Text(CurrencyHelper.currencySymbol(state.investmentCurrency) + " ") },
                    isError = state.amountError != null,
                    supportingText = { state.amountError?.let { Text(it.asString()) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (state.investmentCurrency == "COP") KeyboardType.Number
                        else KeyboardType.Decimal
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = state.dateDisplay,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(InvestmentEntryFormAction.OnDateClick) },
                enabled = false
            )

            OutlinedTextField(
                value = state.notes,
                onValueChange = { onAction(InvestmentEntryFormAction.OnNotesChange(it)) },
                label = { Text("Notas (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onAction(InvestmentEntryFormAction.OnSaveClick) },
                modifier = Modifier.fillMaxWidth(),
                shape = Shapes.large,
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    if (state.showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { onAction(InvestmentEntryFormAction.OnDatePickerDismiss) },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onAction(InvestmentEntryFormAction.OnDateSelected(it))
                    }
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { onAction(InvestmentEntryFormAction.OnDatePickerDismiss) }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { onAction(InvestmentEntryFormAction.OnDeleteDismiss) },
            title = { Text("Eliminar movimiento") },
            text = { Text("¿Eliminar este movimiento?") },
            confirmButton = {
                Button(
                    onClick = { onAction(InvestmentEntryFormAction.OnDeleteConfirm) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { onAction(InvestmentEntryFormAction.OnDeleteDismiss) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
