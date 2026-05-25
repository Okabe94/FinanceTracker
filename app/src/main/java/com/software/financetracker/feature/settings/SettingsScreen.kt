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
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.software.financetracker.R
import com.software.financetracker.core.util.CurrencyHelper
import com.software.financetracker.feature.home.HomeSortField
import com.software.financetracker.feature.investment.list.SortDirection
import com.software.financetracker.feature.investment.list.SortField
import com.software.financetracker.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    snackbarHostState: SnackbarHostState,
    onAction: (SettingsAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { onAction(SettingsAction.OnBackClick) }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
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
            HorizontalDivider()
            HomeCategoriesSection(state, onAction)
            HorizontalDivider()
            InvestmentsSection(state, onAction)
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
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier
            .weight(1f)
            .padding(end = 16.dp)) {
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
        SectionHeader(stringResource(R.string.settings_section_appearance))
        Column {
            Text(
                stringResource(R.string.settings_theme_label),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(8.dp))
            val options = listOf(
                ThemeMode.SYSTEM to stringResource(R.string.settings_theme_system),
                ThemeMode.LIGHT to stringResource(R.string.settings_theme_light),
                ThemeMode.DARK to stringResource(R.string.settings_theme_dark)
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
        SectionHeader(stringResource(R.string.settings_section_currency))
        ExposedDropdownMenuBox(
            expanded = state.showCurrencyDropdown,
            onExpandedChange = { onAction(SettingsAction.OnCurrencyDropdownToggle) }
        ) {
            OutlinedTextField(
                value = state.defaultCurrency,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.settings_currency_label)) },
                supportingText = { Text(stringResource(R.string.settings_currency_hint)) },
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
        SectionHeader(stringResource(R.string.settings_section_exchange_rates))
        SettingsRow(
            label = stringResource(R.string.settings_custom_rates_label),
            description = stringResource(R.string.settings_custom_rates_desc)
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
                    label = { Text(stringResource(R.string.settings_usd_rate_label)) },
                    isError = state.customUsdRateError != null,
                    supportingText = state.customUsdRateError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.customEurRate,
                    onValueChange = { onAction(SettingsAction.OnCustomEurRateChange(it)) },
                    label = { Text(stringResource(R.string.settings_eur_rate_label)) },
                    isError = state.customEurRateError != null,
                    supportingText = state.customEurRateError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.customGbpRate,
                    onValueChange = { onAction(SettingsAction.OnCustomGbpRateChange(it)) },
                    label = { Text(stringResource(R.string.settings_gbp_rate_label)) },
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
                    Text(
                        if (state.isSavingRates) stringResource(R.string.settings_saving_rates_button) else stringResource(
                            R.string.settings_save_rates_button
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationsSection(state: SettingsState, onAction: (SettingsAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(stringResource(R.string.settings_section_notifications))
        SettingsRow(
            label = stringResource(R.string.settings_notifications_label),
            description = stringResource(R.string.settings_notifications_desc)
        ) {
            Switch(
                checked = state.notificationsEnabled,
                onCheckedChange = { onAction(SettingsAction.OnNotificationsToggle(it)) }
            )
        }
    }
}

@Composable
private fun HomeSortField.label(): String = when (this) {
    HomeSortField.ALPHABETICAL -> stringResource(R.string.home_sort_alphabetical)
    HomeSortField.AMOUNT_SPENT -> stringResource(R.string.home_sort_amount_spent)
    HomeSortField.BUDGET_LIMIT -> stringResource(R.string.home_sort_budget_limit)
    HomeSortField.LAST_UPDATED -> stringResource(R.string.home_sort_last_updated)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeCategoriesSection(state: SettingsState, onAction: (SettingsAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(stringResource(R.string.settings_section_home))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.settings_home_sort_field_label),
                style = MaterialTheme.typography.bodyLarge
            )
            ExposedDropdownMenuBox(
                expanded = state.showHomeSortDropdown,
                onExpandedChange = { onAction(SettingsAction.OnHomeSortDropdownToggle) }
            ) {
                OutlinedTextField(
                    value = state.homeSortField.label(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.showHomeSortDropdown)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = state.showHomeSortDropdown,
                    onDismissRequest = { onAction(SettingsAction.OnHomeSortDropdownDismiss) }
                ) {
                    HomeSortField.entries.forEach { field ->
                        DropdownMenuItem(
                            text = { Text(field.label()) },
                            onClick = { onAction(SettingsAction.OnHomeSortFieldSelected(field)) },
                            trailingIcon = if (field == state.homeSortField) {
                                { Text("✓", color = MaterialTheme.colorScheme.primary) }
                            } else null
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.settings_home_sort_direction_label),
                style = MaterialTheme.typography.bodyLarge
            )
            val dirOptions = listOf(
                SortDirection.ASC to stringResource(R.string.settings_investment_sort_asc),
                SortDirection.DESC to stringResource(R.string.settings_investment_sort_desc)
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                dirOptions.forEachIndexed { index, (direction, label) ->
                    SegmentedButton(
                        selected = state.homeSortDirection == direction,
                        onClick = { onAction(SettingsAction.OnHomeSortDirectionChange(direction)) },
                        shape = SegmentedButtonDefaults.itemShape(index, dirOptions.size),
                        label = { Text(label) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SortField.label(): String = when (this) {
    SortField.AMOUNT_INVESTED -> stringResource(R.string.investment_list_sort_amount_invested)
    SortField.PERFORMANCE -> stringResource(R.string.investment_list_sort_performance)
    SortField.ALPHABETICAL -> stringResource(R.string.investment_list_sort_alphabetical)
    SortField.NEWEST -> stringResource(R.string.investment_list_sort_newest)
    SortField.LAST_UPDATED -> stringResource(R.string.investment_list_sort_last_updated)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvestmentsSection(state: SettingsState, onAction: (SettingsAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(stringResource(R.string.settings_section_investments))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.settings_investment_sort_field_label),
                style = MaterialTheme.typography.bodyLarge
            )
            ExposedDropdownMenuBox(
                expanded = state.showInvestmentSortDropdown,
                onExpandedChange = { onAction(SettingsAction.OnInvestmentSortDropdownToggle) }
            ) {
                OutlinedTextField(
                    value = state.investmentSortField.label(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.showInvestmentSortDropdown)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = state.showInvestmentSortDropdown,
                    onDismissRequest = { onAction(SettingsAction.OnInvestmentSortDropdownDismiss) }
                ) {
                    SortField.entries.forEach { field ->
                        DropdownMenuItem(
                            text = { Text(field.label()) },
                            onClick = { onAction(SettingsAction.OnInvestmentSortFieldSelected(field)) },
                            trailingIcon = if (field == state.investmentSortField) {
                                { Text("✓", color = MaterialTheme.colorScheme.primary) }
                            } else null
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.settings_investment_sort_direction_label),
                style = MaterialTheme.typography.bodyLarge
            )
            val dirOptions = listOf(
                SortDirection.ASC to stringResource(R.string.settings_investment_sort_asc),
                SortDirection.DESC to stringResource(R.string.settings_investment_sort_desc)
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                dirOptions.forEachIndexed { index, (direction, label) ->
                    SegmentedButton(
                        selected = state.investmentSortDirection == direction,
                        onClick = { onAction(SettingsAction.OnInvestmentSortDirectionChange(direction)) },
                        shape = SegmentedButtonDefaults.itemShape(index, dirOptions.size),
                        label = { Text(label) }
                    )
                }
            }
        }
    }
}
