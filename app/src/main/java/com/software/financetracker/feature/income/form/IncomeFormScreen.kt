package com.software.financetracker.feature.income.form

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
fun IncomeFormScreen(
    state: IncomeFormState,
    onAction: (IncomeFormAction) -> Unit
) {
    val isEditingRecurring = state.recurringIncomeId != null
    val isEditing = state.incomeId != null || isEditingRecurring

    val recurrenceTypes = listOf(
        RecurrenceType.Daily,
        RecurrenceType.Weekly,
        RecurrenceType.Biweekly,
        RecurrenceType.Monthly,
        RecurrenceType.Custom(state.customIntervalDays)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            isEditingRecurring -> "Editar ingreso recurrente"
                            state.incomeId != null -> "Editar ingreso"
                            else -> "Nuevo ingreso"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(IncomeFormAction.OnBackClick) }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = isEditing,
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
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(state.selectedSourceType.colorArgb).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = state.selectedSourceType.icon,
                                contentDescription = null,
                                tint = Color(state.selectedSourceType.colorArgb),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    trailingIcon = { Icon(Icons.Rounded.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAction(IncomeFormAction.OnSourceDropdownToggle) },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                DropdownMenu(
                    expanded = state.showSourceDropdown,
                    onDismissRequest = { onAction(IncomeFormAction.OnSourceDropdownDismiss) },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    IncomeSourceType.entries.forEachIndexed { index, type ->
                        if (index > 0) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                        DropdownMenuItem(
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(type.colorArgb).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = type.icon,
                                        contentDescription = null,
                                        tint = Color(type.colorArgb),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            },
                            text = {
                                Text(
                                    text = type.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (type == state.selectedSourceType) FontWeight.SemiBold
                                                 else FontWeight.Normal
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = if (type == state.selectedSourceType) MaterialTheme.colorScheme.primary
                                           else Color.Transparent,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = { onAction(IncomeFormAction.OnSourceTypeSelected(type)) },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
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

            OutlinedTextField(
                value = state.notes,
                onValueChange = { onAction(IncomeFormAction.OnNotesChange(it)) },
                label = { Text("Notas (opcional)") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Box(modifier = Modifier.fillMaxWidth().clickable { onAction(IncomeFormAction.OnDateFieldClick) }) {
                OutlinedTextField(
                    value = state.displayDate,
                    onValueChange = {},
                    label = { Text(if (state.isRecurring) "Fecha de inicio" else "Fecha") },
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

            if (!isEditingRecurring) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("¿Se repite?", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = state.isRecurring,
                        onCheckedChange = { onAction(IncomeFormAction.OnToggleRecurring) }
                    )
                }
            }

            if (state.isRecurring) {
                Text("Frecuencia", style = MaterialTheme.typography.labelLarge)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        recurrenceTypes.filterNot { it is RecurrenceType.Custom }.forEach { type ->
                            val isSelected = state.recurrenceType == type
                            FilterChip(
                                selected = isSelected,
                                onClick = { onAction(IncomeFormAction.OnRecurrenceTypeSelect(type)) },
                                label = { Text(type.displayName()) }
                            )
                        }
                    }
                    Row {
                        val customType = recurrenceTypes.first { it is RecurrenceType.Custom }
                        FilterChip(
                            selected = state.recurrenceType is RecurrenceType.Custom,
                            onClick = { onAction(IncomeFormAction.OnRecurrenceTypeSelect(customType)) },
                            label = { Text("Personalizado") }
                        )
                    }
                }

                if (state.recurrenceType is RecurrenceType.Custom) {
                    OutlinedTextField(
                        value = state.customIntervalDays.toString(),
                        onValueChange = { onAction(IncomeFormAction.OnCustomIntervalChange(it.filter { c -> c.isDigit() })) },
                        label = { Text("Cada N días") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (isEditingRecurring) {
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
                        onCheckedChange = { onAction(IncomeFormAction.OnToggleActive) }
                    )
                }
            }

            if (state.showDeleteConfirmDialog) {
                val dialogTitle = if (isEditingRecurring) "Eliminar ingreso recurrente" else "Eliminar ingreso"
                val dialogText = if (isEditingRecurring)
                    "¿Eliminar esta plantilla recurrente? Los ingresos ya generados no se eliminarán."
                else
                    "¿Estás seguro de que deseas eliminar este ingreso?"
                AlertDialog(
                    onDismissRequest = { onAction(IncomeFormAction.OnDeleteDismiss) },
                    title = { Text(dialogTitle) },
                    text = { Text(dialogText) },
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
