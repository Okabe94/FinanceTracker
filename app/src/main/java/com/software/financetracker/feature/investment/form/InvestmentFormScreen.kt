package com.software.financetracker.feature.investment.form

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.software.financetracker.core.util.CurrencyHelper
import com.software.financetracker.ui.components.iconForKey
import com.software.financetracker.ui.theme.Shapes

private val presetColors = listOf(
    0xFFE53935L, 0xFFF4511EL, 0xFFF59300L, 0xFFF6BF26L,
    0xFF33B679L, 0xFF0B8043L, 0xFF009688L, 0xFF039BE5L,
    0xFF3F51B5L, 0xFF7986CBL, 0xFFE67C73L, 0xFF616161L
).map { it.toInt() }

private val presetInvestmentIcons = listOf(
    "trending_up", "account_balance", "savings", "currency_bitcoin",
    "real_estate_agent", "work", "flight", "school",
    "shopping_cart", "fitness_center", "more_horiz"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentFormScreen(
    state: InvestmentFormState,
    onAction: (InvestmentFormAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.investmentId == null) "Nueva inversión" else "Editar inversión") },
                navigationIcon = {
                    IconButton(onClick = { onAction(InvestmentFormAction.OnBackClick) }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (state.investmentId != null) {
                        IconButton(onClick = { onAction(InvestmentFormAction.OnDeleteClick) }) {
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

            OutlinedTextField(
                value = state.name,
                onValueChange = { onAction(InvestmentFormAction.OnNameChange(it)) },
                label = { Text("Nombre") },
                isError = state.nameError != null,
                supportingText = { state.nameError?.let { Text(it.asString()) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = state.showCurrencyDropdown,
                onExpandedChange = { onAction(InvestmentFormAction.OnCurrencyDropdownToggle) }
            ) {
                OutlinedTextField(
                    value = state.selectedCurrency,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Moneda") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.showCurrencyDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = state.showCurrencyDropdown,
                    onDismissRequest = { onAction(InvestmentFormAction.OnCurrencyDropdownToggle) }
                ) {
                    CurrencyHelper.supportedCurrencies.forEach { currency ->
                        DropdownMenuItem(
                            text = { Text(currency) },
                            onClick = { onAction(InvestmentFormAction.OnCurrencySelected(currency)) }
                        )
                    }
                }
            }

            Text("Color", style = MaterialTheme.typography.labelLarge)
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(presetColors) { argb ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(argb))
                            .clickable { onAction(InvestmentFormAction.OnColorSelected(argb)) },
                        contentAlignment = Alignment.Center
                    ) {
                        val checkAnim by animateFloatAsState(
                            targetValue = if (argb == state.selectedColorArgb) 1f else 0f,
                            animationSpec = tween(150),
                            label = "checkAnim"
                        )
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .graphicsLayer {
                                    alpha = checkAnim
                                    scaleX = 0.5f + checkAnim * 0.5f
                                    scaleY = 0.5f + checkAnim * 0.5f
                                }
                        )
                    }
                }
            }

            Text("Ícono", style = MaterialTheme.typography.labelLarge)
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(presetInvestmentIcons) { key ->
                    val selected = key == state.selectedIconKey
                    val bgColor by animateColorAsState(
                        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant,
                        animationSpec = tween(200),
                        label = "iconBg"
                    )
                    val iconTint by animateColorAsState(
                        targetValue = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(200),
                        label = "iconTint"
                    )
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(bgColor)
                            .clickable { onAction(InvestmentFormAction.OnIconSelected(key)) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconForKey(key),
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tasa de retorno fija", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = state.hasFixedRoi,
                    onCheckedChange = { onAction(InvestmentFormAction.OnFixedRoiToggle(it)) }
                )
            }

            AnimatedVisibility(
                visible = state.hasFixedRoi,
                enter = expandVertically(animationSpec = tween(250), expandFrom = Alignment.Top) +
                    fadeIn(animationSpec = tween(200)),
                exit = shrinkVertically(animationSpec = tween(200), shrinkTowards = Alignment.Top) +
                    fadeOut(animationSpec = tween(150))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = state.annualRateInput,
                        onValueChange = { onAction(InvestmentFormAction.OnAnnualRateChange(it)) },
                        label = { Text("Tasa anual (%)") },
                        isError = state.annualRateError != null,
                        supportingText = { state.annualRateError?.let { Text(it.asString()) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        suffix = { Text("%") }
                    )
                    OutlinedTextField(
                        value = state.maturityDateDisplay ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fecha de vencimiento (opcional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAction(InvestmentFormAction.OnMaturityDateClick) },
                        enabled = false
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onAction(InvestmentFormAction.OnSaveClick) },
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

    if (state.showMaturityDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { onAction(InvestmentFormAction.OnMaturityDatePickerDismiss) },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onAction(InvestmentFormAction.OnMaturityDateSelected(it))
                    }
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { onAction(InvestmentFormAction.OnMaturityDatePickerDismiss) }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { onAction(InvestmentFormAction.OnDeleteDismiss) },
            title = { Text("Eliminar inversión") },
            text = { Text("¿Estás seguro? Se eliminarán todos los movimientos de esta inversión.") },
            confirmButton = {
                Button(
                    onClick = { onAction(InvestmentFormAction.OnDeleteConfirm) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { onAction(InvestmentFormAction.OnDeleteDismiss) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
