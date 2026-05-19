package com.software.financetracker.feature.recurring.form

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
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringExpenseFormScreen(
    state: RecurringExpenseFormState,
    onAction: (RecurringExpenseFormAction) -> Unit
) {
    val recurrenceTypes = listOf(
        RecurrenceType.Daily,
        RecurrenceType.Weekly,
        RecurrenceType.Biweekly,
        RecurrenceType.Monthly
    )
    val selectedCategoryName = state.categories.firstOrNull { it.id == state.categoryId }?.name
        ?: "Seleccionar categoría"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (state.recurringExpenseId == null) "Nuevo gasto recurrente" else "Editar recurrente")
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(RecurringExpenseFormAction.OnBackClick) }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = state.recurringExpenseId != null,
                        enter = fadeIn(animationSpec = tween(200)) +
                            scaleIn(initialScale = 0.8f, animationSpec = tween(200)),
                        exit = fadeOut(animationSpec = tween(150)) +
                            scaleOut(targetScale = 0.8f, animationSpec = tween(150))
                    ) {
                        IconButton(onClick = { onAction(RecurringExpenseFormAction.OnDeleteClick) }) {
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
            Box {
                OutlinedTextField(
                    value = selectedCategoryName,
                    onValueChange = {},
                    label = { Text("Categoría") },
                    readOnly = true,
                    isError = state.categoryError,
                    supportingText = if (state.categoryError) {
                        { Text("Selecciona una categoría") }
                    } else null,
                    trailingIcon = {
                        Icon(Icons.Rounded.ArrowDropDown, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAction(RecurringExpenseFormAction.OnCategoryDropdownToggle) },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = if (state.categoryError) MaterialTheme.colorScheme.error
                                              else MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                DropdownMenu(
                    expanded = state.showCategoryDropdown,
                    onDismissRequest = { onAction(RecurringExpenseFormAction.OnCategoryDropdownDismiss) }
                ) {
                    state.categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = { onAction(RecurringExpenseFormAction.OnCategorySelected(cat.id)) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.amountInput,
                onValueChange = { onAction(RecurringExpenseFormAction.OnAmountChange(it.filter { c -> c.isDigit() })) },
                label = { Text("Monto (COP)") },
                prefix = { Text("$ ") },
                isError = state.amountError != null,
                supportingText = { state.amountError?.let { Text(it.asString()) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CopVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { onAction(RecurringExpenseFormAction.OnDescriptionChange(it)) },
                label = { Text("Descripción (opcional)") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Frecuencia", style = MaterialTheme.typography.labelLarge)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                recurrenceTypes.forEach { type ->
                    FilterChip(
                        selected = state.recurrenceType == type,
                        onClick = { onAction(RecurringExpenseFormAction.OnRecurrenceTypeChange(type)) },
                        label = { Text(type.displayName()) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(RecurringExpenseFormAction.OnDateFieldClick) }
            ) {
                OutlinedTextField(
                    value = state.displayDate,
                    onValueChange = {},
                    label = { Text(if (state.recurringExpenseId == null) "Fecha de inicio" else "Fecha de inicio") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Rounded.CalendarToday, contentDescription = "Seleccionar fecha")
                    },
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

            if (state.recurringExpenseId != null) {
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
                        onCheckedChange = { onAction(RecurringExpenseFormAction.OnActiveToggle(it)) }
                    )
                }
            }

            if (state.showDeleteConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { onAction(RecurringExpenseFormAction.OnDeleteDismiss) },
                    title = { Text("Eliminar gasto recurrente") },
                    text = { Text("¿Eliminar esta plantilla recurrente? Los gastos ya generados no se eliminarán.") },
                    confirmButton = {
                        TextButton(onClick = { onAction(RecurringExpenseFormAction.OnDeleteConfirm) }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { onAction(RecurringExpenseFormAction.OnDeleteDismiss) }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            if (state.showDatePicker) {
                val initialMillis = remember(state.selectedDateStorage) {
                    LocalDate.parse(state.selectedDateStorage, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .atStartOfDay(ZoneOffset.UTC)
                        .toInstant()
                        .toEpochMilli()
                }
                val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
                DatePickerDialog(
                    onDismissRequest = { onAction(RecurringExpenseFormAction.OnDatePickerDismiss) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                pickerState.selectedDateMillis?.let {
                                    onAction(RecurringExpenseFormAction.OnDateSelected(it))
                                }
                            }
                        ) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { onAction(RecurringExpenseFormAction.OnDatePickerDismiss) }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(
                        state = pickerState,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onAction(RecurringExpenseFormAction.OnSaveClick) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
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
        }
    }
}
