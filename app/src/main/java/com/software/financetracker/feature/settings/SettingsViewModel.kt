package com.software.financetracker.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.software.financetracker.core.preferences.UserPreferences
import com.software.financetracker.data.local.investment.ExchangeRateEntity
import com.software.financetracker.domain.repository.ExchangeRateRepository
import com.software.financetracker.ui.theme.ThemeMode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SettingsViewModel(
    private val prefs: UserPreferences,
    private val exchangeRateRepo: ExchangeRateRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _events = Channel<SettingsEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                prefs.themeMode,
                prefs.defaultCurrency,
                prefs.useCustomExchangeRates,
                prefs.notificationsEnabled
            ) { theme, currency, useCustom, notifications ->
                _state.value.copy(
                    themeMode = theme,
                    defaultCurrency = currency,
                    useCustomExchangeRates = useCustom,
                    notificationsEnabled = notifications,
                    isLoading = false
                )
            }.collect { newState -> _state.value = newState }
        }

        viewModelScope.launch {
            val rates = exchangeRateRepo.getAll().first()
            val usd = rates.find { it.fromCurrency == "USD" }?.rate?.toFloat()
            val eur = rates.find { it.fromCurrency == "EUR" }?.rate?.toFloat()
            val gbp = rates.find { it.fromCurrency == "GBP" }?.rate?.toFloat()
            _state.update { s ->
                s.copy(
                    customUsdRate = usd?.let { formatRate(it) } ?: s.customUsdRate,
                    customEurRate = eur?.let { formatRate(it) } ?: s.customEurRate,
                    customGbpRate = gbp?.let { formatRate(it) } ?: s.customGbpRate
                )
            }
        }
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.OnBackClick ->
                viewModelScope.launch { _events.send(SettingsEvent.NavigateBack) }

            is SettingsAction.OnThemeModeChange ->
                viewModelScope.launch { prefs.setThemeMode(action.mode) }

            is SettingsAction.OnDefaultCurrencySelected ->
                viewModelScope.launch {
                    prefs.setDefaultCurrency(action.currency)
                    _state.update { it.copy(showCurrencyDropdown = false) }
                }

            SettingsAction.OnCurrencyDropdownToggle ->
                _state.update { it.copy(showCurrencyDropdown = !it.showCurrencyDropdown) }

            SettingsAction.OnCurrencyDropdownDismiss ->
                _state.update { it.copy(showCurrencyDropdown = false) }

            is SettingsAction.OnUseCustomRatesToggle -> {
                viewModelScope.launch {
                    prefs.setUseCustomExchangeRates(action.enabled)
                    if (!action.enabled) {
                        exchangeRateRepo.refresh()
                        val rates = exchangeRateRepo.getAll().first()
                        val usd = rates.find { it.fromCurrency == "USD" }?.rate?.toFloat()
                        val eur = rates.find { it.fromCurrency == "EUR" }?.rate?.toFloat()
                        val gbp = rates.find { it.fromCurrency == "GBP" }?.rate?.toFloat()
                        _state.update { s ->
                            s.copy(
                                customUsdRate = usd?.let { formatRate(it) } ?: s.customUsdRate,
                                customEurRate = eur?.let { formatRate(it) } ?: s.customEurRate,
                                customGbpRate = gbp?.let { formatRate(it) } ?: s.customGbpRate
                            )
                        }
                    }
                }
            }

            is SettingsAction.OnCustomUsdRateChange ->
                _state.update { it.copy(customUsdRate = action.value, customUsdRateError = null) }

            is SettingsAction.OnCustomEurRateChange ->
                _state.update { it.copy(customEurRate = action.value, customEurRateError = null) }

            is SettingsAction.OnCustomGbpRateChange ->
                _state.update { it.copy(customGbpRate = action.value, customGbpRateError = null) }

            SettingsAction.OnSaveCustomRates -> saveCustomRates()

            is SettingsAction.OnNotificationsToggle ->
                viewModelScope.launch { prefs.setNotificationsEnabled(action.enabled) }
        }
    }

    private fun saveCustomRates() {
        val s = _state.value
        val usd = s.customUsdRate.replace(",", ".").toFloatOrNull()
        val eur = s.customEurRate.replace(",", ".").toFloatOrNull()
        val gbp = s.customGbpRate.replace(",", ".").toFloatOrNull()

        var hasError = false
        if (usd == null || usd <= 0f) {
            _state.update { it.copy(customUsdRateError = "Ingresa una tasa válida") }
            hasError = true
        }
        if (eur == null || eur <= 0f) {
            _state.update { it.copy(customEurRateError = "Ingresa una tasa válida") }
            hasError = true
        }
        if (gbp == null || gbp <= 0f) {
            _state.update { it.copy(customGbpRateError = "Ingresa una tasa válida") }
            hasError = true
        }
        if (hasError) return

        _state.update { it.copy(isSavingRates = true) }
        viewModelScope.launch {
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            listOf(
                ExchangeRateEntity("USD", "COP", usd!!.toDouble(), timestamp),
                ExchangeRateEntity("EUR", "COP", eur!!.toDouble(), timestamp),
                ExchangeRateEntity("GBP", "COP", gbp!!.toDouble(), timestamp)
            ).forEach { exchangeRateRepo.upsert(it) }
            prefs.setCustomRates(usd, eur, gbp)
            _state.update { it.copy(isSavingRates = false) }
            _events.send(SettingsEvent.ShowSnackbar("Tasas guardadas"))
        }
    }

    private fun formatRate(value: Float): String =
        if (value == value.toLong().toFloat()) value.toLong().toString()
        else "%.2f".format(value)
}
