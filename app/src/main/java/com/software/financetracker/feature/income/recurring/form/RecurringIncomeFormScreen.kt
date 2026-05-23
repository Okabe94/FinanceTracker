package com.software.financetracker.feature.income.recurring.form

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.software.financetracker.core.presentation.CopVisualTransformation
import com.software.financetracker.domain.model.RecurrenceType
import com.software.financetracker.domain.model.displayName
import com.software.financetracker.feature.income.IncomeSourceType
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringIncomeFormScreen(
    state: RecurringIncomeFormState,
    onAction: (RecurringIncomeFormAction) -> Unit
) {
    val recurrenceTypes = listOf(RecurrenceType.Daily, RecurrenceType.Weekly, RecurrenceType.Biweekly, RecurrenceType.Monthly)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.recurringIncomeId == null) "Nuevo ingreso recurrente" else "Editar recurrente") },
                navigationIcon = {
                    IconButton(onClick = { onAction(RecurringIncomeFormAction.OnBackClick) }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = state.recurringIncomeId != null,
                        enter = fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.8f, animationSpec = tween(200)),
                        exit = fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.8f, animationSpec = tween(150))
                    ) {
                        IconButton(onClick = { onAction(RecurringIncomeFormAction.OnDeleteClick) }) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.amountInput,
                onValueChange = { onAction(RecurringIncomeFormAction.OnAmountChange(it.filter { c -> c.isDigit() })) },
                label = { Text("Monto (COP)") },
                prefix = { Text("$ ") },
                isError = state.amountError != null,
                supportingText = { state.amountError?.let { Text(it.asString()) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CopVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Box {
                OutlinedTextField(
                    value = state.selectedSourceType.displayName,
                    onValueChange = {},
                    label = { Text("Tipo de ingreso") },
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Rounded.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAction(RecurringIncomeFormAction.OnSourceDropdownToggle) },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                DropdownMenu(
                    expanded = state.showSourceDropdown,
                    onDismissRequest = { onAction(RecurringIncomeFormAction.OnSourceDropdownDismiss) }
                ) {
                    IncomeSourceType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = { onAction(RecurringIncomeFormAction.OnSourceTypeSelected(type)) }
                        )
                    }
                }
            }

            if (state.selectedSourceType == IncomeSourceType.OTHER) {
                OutlinedTextField(
                    value = state.customSource,
                    onValueChange = { onAction(RecurringIncomeFormAction.OnCustomSourceChange(it)) },
                    label = { Text("Especifica la fuente") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = state.notes,
                onValueChange = { onAction(RecurringIncomeFormAction.OnNotesChange(it)) },
                label = { Text("Notas (opcional)") },
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Frecuencia", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                recurrenceTypes.forEach { type ->
                    FilterChip(
                        selected = state.recurrenceType == type,
                        onClick = { onAction(RecurringIncomeFormAction.OnRecurrenceTypeChange(type)) },
                        label = { Text(type.displayName()) }
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().clickable { onAction(RecurringIncomeFormAction.OnDateFieldClick) }
            ) {
                OutlinedTextField(
                    value = state.displayDate,
                    onValueChange = {},
                    label = { Text("Fecha de inicio") },
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Rounded.CalendarToday, contentDescription = "Seleccionar fecha") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            if (state.recurringIncomeId != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Activo", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "Desactivar para pausar sin eliminar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.isActive,
                        onCheckedChange = { onAction(RecurringIncomeFormAction.OnActiveToggle(it)) }
                    )
                }
            }

            if (state.showDeleteConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { onAction(RecurringIncomeFormAction.OnDeleteDismiss) },
                    title = { Text("Eliminar ingreso recurrente") },
                    text = { Text("¿Eliminar esta plantilla? Los ingresos ya generados no se eliminarán.") },
                    confirmButton = {
                        TextButton(onClick = { onAction(RecurringIncomeFormAction.OnDeleteConfirm) }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { onAction(RecurringIncomeFormAction.OnDeleteDismiss) }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            if (state.showDatePicker) {
                val initialMillis = remember(state.selectedDateStorage) {
                    LocalDate.parse(state.selectedDateStorage, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                }
                val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
                DatePickerDialog(
                    onDismissRequest = { onAction(RecurringIncomeFormAction.OnDatePickerDismiss) },
                    confirmButton = {
                        TextButton(onClick = {
                            pickerState.selectedDateMillis?.let { onAction(RecurringIncomeFormAction.OnDateSelected(it)) }
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { onAction(RecurringIncomeFormAction.OnDatePickerDismiss) }) { Text("Cancelar") }
                    }
                ) {
                    DatePicker(state = pickerState, modifier = Modifier.verticalScroll(rememberScrollState()))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onAction(RecurringIncomeFormAction.OnSaveClick) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar")
                }
            }
        }
    }
}
