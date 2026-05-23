package com.software.financetracker.feature.income.form

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.software.financetracker.core.presentation.CopVisualTransformation
import com.software.financetracker.feature.income.IncomeSourceType
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeFormScreen(
    state: IncomeFormState,
    onAction: (IncomeFormAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.incomeId == null) "Nuevo ingreso" else "Editar ingreso") },
                navigationIcon = {
                    IconButton(onClick = { onAction(IncomeFormAction.OnBackClick) }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = state.incomeId != null,
                        enter = fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.8f, animationSpec = tween(200)),
                        exit = fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.8f, animationSpec = tween(150))
                    ) {
                        IconButton(onClick = { onAction(IncomeFormAction.OnDeleteClick) }) {
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
                onValueChange = { onAction(IncomeFormAction.OnAmountChange(it.filter { c -> c.isDigit() })) },
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
                        .clickable { onAction(IncomeFormAction.OnSourceDropdownToggle) },
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
                    onDismissRequest = { onAction(IncomeFormAction.OnSourceDropdownDismiss) }
                ) {
                    IncomeSourceType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = { onAction(IncomeFormAction.OnSourceTypeSelected(type)) }
                        )
                    }
                }
            }

            if (state.selectedSourceType == IncomeSourceType.OTHER) {
                OutlinedTextField(
                    value = state.customSource,
                    onValueChange = { onAction(IncomeFormAction.OnCustomSourceChange(it)) },
                    label = { Text("Especifica la fuente") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Box(modifier = Modifier.fillMaxWidth().clickable { onAction(IncomeFormAction.OnDateFieldClick) }) {
                OutlinedTextField(
                    value = state.displayDate,
                    onValueChange = {},
                    label = { Text("Fecha") },
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

            OutlinedTextField(
                value = state.notes,
                onValueChange = { onAction(IncomeFormAction.OnNotesChange(it)) },
                label = { Text("Notas (opcional)") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.showDeleteConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { onAction(IncomeFormAction.OnDeleteDismiss) },
                    title = { Text("Eliminar ingreso") },
                    text = { Text("¿Estás seguro de que deseas eliminar este ingreso?") },
                    confirmButton = {
                        TextButton(onClick = { onAction(IncomeFormAction.OnDeleteConfirm) }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { onAction(IncomeFormAction.OnDeleteDismiss) }) { Text("Cancelar") }
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
                    onDismissRequest = { onAction(IncomeFormAction.OnDatePickerDismiss) },
                    confirmButton = {
                        TextButton(onClick = {
                            pickerState.selectedDateMillis?.let { onAction(IncomeFormAction.OnDateSelected(it)) }
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { onAction(IncomeFormAction.OnDatePickerDismiss) }) { Text("Cancelar") }
                    }
                ) {
                    DatePicker(state = pickerState, modifier = Modifier.verticalScroll(rememberScrollState()))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onAction(IncomeFormAction.OnSaveClick) },
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
