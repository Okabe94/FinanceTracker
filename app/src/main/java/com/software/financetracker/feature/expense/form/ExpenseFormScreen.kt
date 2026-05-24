package com.software.financetracker.feature.expense.form

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
import com.software.financetracker.ui.components.iconForKey
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseFormScreen(
    state: ExpenseFormState,
    onAction: (ExpenseFormAction) -> Unit
) {
    val isEditingRecurring = state.recurringExpenseId != null
    val isEditing = state.expenseId != null || isEditingRecurring
    val showCategoryPicker = state.categoryId == null || state.categories.isNotEmpty()
    val selectedCategoryName = state.categories.firstOrNull { it.id == state.categoryId }?.name
        ?: "Seleccionar categoría"

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
                            isEditingRecurring -> "Editar recurrente"
                            state.expenseId != null -> "Editar gasto"
                            else -> "Nuevo gasto"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(ExpenseFormAction.OnBackClick) }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = isEditing,
                        enter = fadeIn(animationSpec = tween(200)) +
                            scaleIn(initialScale = 0.8f, animationSpec = tween(200)),
                        exit = fadeOut(animationSpec = tween(150)) +
                            scaleOut(targetScale = 0.8f, animationSpec = tween(150))
                    ) {
                        IconButton(onClick = { onAction(ExpenseFormAction.OnDeleteClick) }) {
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
            if (showCategoryPicker) {
                val selectedCat = state.categories.firstOrNull { it.id == state.categoryId }
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
                        leadingIcon = {
                            if (selectedCat != null) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(selectedCat.colorArgb).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = iconForKey(selectedCat.iconKey),
                                        contentDescription = null,
                                        tint = Color(selectedCat.colorArgb),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        trailingIcon = {
                            Icon(Icons.Rounded.ArrowDropDown, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAction(ExpenseFormAction.OnToggleCategoryDropdown) },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = if (state.categoryError) MaterialTheme.colorScheme.error
                                                  else MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    DropdownMenu(
                        expanded = state.showCategoryDropdown,
                        onDismissRequest = { onAction(ExpenseFormAction.OnCategoryDropdownDismiss) },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        state.categories.forEachIndexed { index, cat ->
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
                                            .background(Color(cat.colorArgb).copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = iconForKey(cat.iconKey),
                                            contentDescription = null,
                                            tint = Color(cat.colorArgb),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                },
                                text = {
                                    Text(
                                        text = cat.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (cat.id == state.categoryId) FontWeight.SemiBold
                                                     else FontWeight.Normal
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = if (cat.id == state.categoryId) MaterialTheme.colorScheme.primary
                                               else Color.Transparent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = { onAction(ExpenseFormAction.OnCategorySelect(cat.id)) },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    horizontal = 12.dp, vertical = 4.dp
                                )
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = state.amountInput,
                onValueChange = { onAction(ExpenseFormAction.OnAmountChange(it.filter { c -> c.isDigit() })) },
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
                onValueChange = { onAction(ExpenseFormAction.OnDescriptionChange(it)) },
                label = { Text("Descripción (opcional)") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(ExpenseFormAction.OnDateFieldClick) }
            ) {
                OutlinedTextField(
                    value = state.displayDate,
                    onValueChange = {},
                    label = { Text(if (state.isRecurring) "Fecha de inicio" else "Fecha") },
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

            if (!isEditingRecurring) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("¿Se repite?", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = state.isRecurring,
                        onCheckedChange = { onAction(ExpenseFormAction.OnToggleRecurring) }
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
                                onClick = { onAction(ExpenseFormAction.OnRecurrenceTypeSelect(type)) },
                                label = { Text(type.displayName()) }
                            )
                        }
                    }
                    Row {
                        val customType = recurrenceTypes.first { it is RecurrenceType.Custom }
                        FilterChip(
                            selected = state.recurrenceType is RecurrenceType.Custom,
                            onClick = { onAction(ExpenseFormAction.OnRecurrenceTypeSelect(customType)) },
                            label = { Text("Personalizado") }
                        )
                    }
                }

                if (state.recurrenceType is RecurrenceType.Custom) {
                    OutlinedTextField(
                        value = state.customIntervalDays.toString(),
                        onValueChange = { onAction(ExpenseFormAction.OnCustomIntervalChange(it.filter { c -> c.isDigit() })) },
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
                        onCheckedChange = { onAction(ExpenseFormAction.OnToggleActive) }
                    )
                }
            }

            if (state.showDeleteConfirmDialog) {
                val dialogTitle = if (isEditingRecurring) "Eliminar gasto recurrente" else "Eliminar gasto"
                val dialogText = if (isEditingRecurring)
                    "¿Eliminar esta plantilla recurrente? Los gastos ya generados no se eliminarán."
                else
                    "¿Eliminar este gasto? Esta acción no se puede deshacer."
                AlertDialog(
                    onDismissRequest = { onAction(ExpenseFormAction.OnDeleteDismiss) },
                    title = { Text(dialogTitle) },
                    text = { Text(dialogText) },
                    confirmButton = {
                        TextButton(onClick = { onAction(ExpenseFormAction.OnDeleteConfirm) }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { onAction(ExpenseFormAction.OnDeleteDismiss) }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            if (state.showDatePicker) {
                val initialMillis = remember(state.selectedDateStorage) {
                    LocalDate.parse(
                        state.selectedDateStorage,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    ).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                }
                val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
                DatePickerDialog(
                    onDismissRequest = { onAction(ExpenseFormAction.OnDatePickerDismiss) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                pickerState.selectedDateMillis?.let {
                                    onAction(ExpenseFormAction.OnDateSelected(it))
                                }
                            }
                        ) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { onAction(ExpenseFormAction.OnDatePickerDismiss) }) {
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
                onClick = { onAction(ExpenseFormAction.OnSaveClick) },
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
