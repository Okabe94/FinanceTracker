package com.software.financetracker.feature.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.software.financetracker.core.util.CurrencyHelper
import com.software.financetracker.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    snackbarHostState: SnackbarHostState,
    onAction: (SettingsAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = { onAction(SettingsAction.OnBackClick) }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AppearanceSection(state, onAction)
            HorizontalDivider()
            CurrencySection(state, onAction)
            HorizontalDivider()
            ExchangeRatesSection(state, onAction)
            HorizontalDivider()
            NotificationsSection(state, onAction)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun SettingsRow(
    label: String,
    description: String? = null,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            if (description != null) {
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppearanceSection(state: SettingsState, onAction: (SettingsAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Apariencia")
        Column {
            Text("Tema", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            val options = listOf(
                ThemeMode.SYSTEM to "Sistema",
                ThemeMode.LIGHT to "Claro",
                ThemeMode.DARK to "Oscuro"
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                options.forEachIndexed { index, (mode, label) ->
                    SegmentedButton(
                        selected = state.themeMode == mode,
                        onClick = { onAction(SettingsAction.OnThemeModeChange(mode)) },
                        shape = SegmentedButtonDefaults.itemShape(index, options.size),
                        label = { Text(label) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencySection(state: SettingsState, onAction: (SettingsAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Moneda")
        ExposedDropdownMenuBox(
            expanded = state.showCurrencyDropdown,
            onExpandedChange = { onAction(SettingsAction.OnCurrencyDropdownToggle) }
        ) {
            OutlinedTextField(
                value = state.defaultCurrency,
                onValueChange = {},
                readOnly = true,
                label = { Text("Moneda predeterminada") },
                supportingText = { Text("Usada al crear nuevas inversiones") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.showCurrencyDropdown)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = state.showCurrencyDropdown,
                onDismissRequest = { onAction(SettingsAction.OnCurrencyDropdownDismiss) }
            ) {
                CurrencyHelper.supportedCurrencies.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currency) },
                        onClick = { onAction(SettingsAction.OnDefaultCurrencySelected(currency)) },
                        trailingIcon = if (currency == state.defaultCurrency) {
                            { Text("✓", color = MaterialTheme.colorScheme.primary) }
                        } else null
                    )
                }
            }
        }
    }
}

@Composable
private fun ExchangeRatesSection(state: SettingsState, onAction: (SettingsAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Tasas de cambio")
        SettingsRow(
            label = "Usar tasas propias",
            description = "Desactiva la consulta automática a la API y permite ingresar tus propias tasas"
        ) {
            Switch(
                checked = state.useCustomExchangeRates,
                onCheckedChange = { onAction(SettingsAction.OnUseCustomRatesToggle(it)) }
            )
        }
        AnimatedVisibility(
            visible = state.useCustomExchangeRates,
            enter = expandVertically(animationSpec = tween(250), expandFrom = Alignment.Top) +
                fadeIn(animationSpec = tween(200)),
            exit = shrinkVertically(animationSpec = tween(200), shrinkTowards = Alignment.Top) +
                fadeOut(animationSpec = tween(150))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.customUsdRate,
                    onValueChange = { onAction(SettingsAction.OnCustomUsdRateChange(it)) },
                    label = { Text("USD → COP") },
                    isError = state.customUsdRateError != null,
                    supportingText = state.customUsdRateError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.customEurRate,
                    onValueChange = { onAction(SettingsAction.OnCustomEurRateChange(it)) },
                    label = { Text("EUR → COP") },
                    isError = state.customEurRateError != null,
                    supportingText = state.customEurRateError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.customGbpRate,
                    onValueChange = { onAction(SettingsAction.OnCustomGbpRateChange(it)) },
                    label = { Text("GBP → COP") },
                    isError = state.customGbpRateError != null,
                    supportingText = state.customGbpRateError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onAction(SettingsAction.OnSaveCustomRates) },
                    enabled = !state.isSavingRates,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (state.isSavingRates) "Guardando..." else "Guardar tasas")
                }
            }
        }
    }
}

@Composable
private fun NotificationsSection(state: SettingsState, onAction: (SettingsAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Notificaciones")
        SettingsRow(
            label = "Habilitar notificaciones",
            description = "Alertas de presupuesto cuando superas o te acercas al límite de una categoría"
        ) {
            Switch(
                checked = state.notificationsEnabled,
                onCheckedChange = { onAction(SettingsAction.OnNotificationsToggle(it)) }
            )
        }
    }
}
