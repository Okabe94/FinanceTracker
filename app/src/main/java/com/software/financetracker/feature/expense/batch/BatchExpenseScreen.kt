package com.software.financetracker.feature.expense.batch

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.software.financetracker.core.presentation.CopVisualTransformation
import com.software.financetracker.feature.expense.form.CategoryItem
import com.software.financetracker.ui.components.iconForKey
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchExpenseScreen(
    state: BatchExpenseState,
    onAction: (BatchExpenseAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar gastos") },
                navigationIcon = {
                    IconButton(onClick = { onAction(BatchExpenseAction.OnBackClick) }) {
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
                    TextButton(onClick = { onAction(BatchExpenseAction.OnAddRow) }) {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Agregar fila")
                    }
                    Button(
                        onClick = { onAction(BatchExpenseAction.OnSaveAll) },
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
                ExpenseRowCard(
                    row = row,
                    index = index,
                    categories = state.categories,
                    isDropdownOpen = state.openDropdownRowId == row.rowId,
                    canRemove = state.rows.size > 1,
                    onAction = onAction
                )
            }
        }
    }

    // Date picker overlay — shown for the row that requested it
    val activePickerRow = state.rows.firstOrNull { it.showDatePicker }
    if (activePickerRow != null) {
        val initialMillis = remember(activePickerRow.dateStorage) {
            if (activePickerRow.dateStorage.isNotEmpty()) {
                LocalDate.parse(activePickerRow.dateStorage, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            } else null
        }
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { onAction(BatchExpenseAction.OnDismissDatePicker) },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let {
                        onAction(BatchExpenseAction.OnDateSelected(activePickerRow.rowId, it))
                    }
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { onAction(BatchExpenseAction.OnDismissDatePicker) }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun ExpenseRowCard(
    row: ExpenseRowItem,
    index: Int,
    categories: List<CategoryItem>,
    isDropdownOpen: Boolean,
    canRemove: Boolean,
    onAction: (BatchExpenseAction) -> Unit
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
                    "Gasto ${index + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (canRemove) {
                    IconButton(
                        onClick = { onAction(BatchExpenseAction.OnRemoveRow(row.rowId)) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Eliminar fila", modifier = Modifier.size(18.dp))
                    }
                }
            }

            CategoryDropdownField(
                row = row,
                categories = categories,
                isDropdownOpen = isDropdownOpen,
                onAction = onAction
            )

            OutlinedTextField(
                value = row.amountInput,
                onValueChange = { onAction(BatchExpenseAction.OnAmountChange(row.rowId, it.filter { c -> c.isDigit() })) },
                label = { Text("Monto") },
                prefix = { Text("$ ") },
                isError = row.amountError != null,
                supportingText = { row.amountError?.let { Text(it.asString()) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CopVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = row.description,
                onValueChange = { onAction(BatchExpenseAction.OnDescriptionChange(row.rowId, it)) },
                label = { Text("Descripción (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(BatchExpenseAction.OnDateFieldClick(row.rowId)) }
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

@Composable
private fun CategoryDropdownField(
    row: ExpenseRowItem,
    categories: List<CategoryItem>,
    isDropdownOpen: Boolean,
    onAction: (BatchExpenseAction) -> Unit
) {
    val selectedCat = categories.firstOrNull { it.id == row.categoryId }
    val label = selectedCat?.name ?: "Categoría"

    Box {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            label = { Text("Categoría") },
            readOnly = true,
            isError = row.categoryError,
            supportingText = if (row.categoryError) {
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
                .clickable { onAction(BatchExpenseAction.OnToggleCategoryDropdown(row.rowId)) },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = if (row.categoryError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        DropdownMenu(
            expanded = isDropdownOpen,
            onDismissRequest = { onAction(BatchExpenseAction.OnDismissCategoryDropdown) },
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            categories.forEachIndexed { index, cat ->
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
                            fontWeight = if (cat.id == row.categoryId) FontWeight.SemiBold
                            else FontWeight.Normal
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = if (cat.id == row.categoryId) MaterialTheme.colorScheme.primary
                            else Color.Transparent,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = { onAction(BatchExpenseAction.OnCategorySelect(row.rowId, cat.id)) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}
