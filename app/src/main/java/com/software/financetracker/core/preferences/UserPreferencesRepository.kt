package com.software.financetracker.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.software.financetracker.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) : UserPreferences {
    private val notificationsEnabledKey = booleanPreferencesKey("notifications_enabled")
    private val themeModeKey = stringPreferencesKey("theme_mode")
    private val defaultCurrencyKey = stringPreferencesKey("default_currency")
    private val useCustomExchangeRatesKey = booleanPreferencesKey("use_custom_exchange_rates")
    private val customUsdRateKey = floatPreferencesKey("custom_usd_rate")
    private val customEurRateKey = floatPreferencesKey("custom_eur_rate")
    private val customGbpRateKey = floatPreferencesKey("custom_gbp_rate")
    private val investmentSortFieldKey = stringPreferencesKey("investment_sort_field")
    private val investmentSortDirectionKey = stringPreferencesKey("investment_sort_direction")
    private val homeSortFieldKey = stringPreferencesKey("home_sort_field")
    private val homeSortDirectionKey = stringPreferencesKey("home_sort_direction")

    override val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[notificationsEnabledKey] ?: true }

    override val themeMode: Flow<ThemeMode> = context.dataStore.data
        .map { prefs ->
            when (prefs[themeModeKey]) {
                "system" -> ThemeMode.SYSTEM
                "light" -> ThemeMode.LIGHT
                else -> ThemeMode.DARK
            }
        }

    override val defaultCurrency: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[defaultCurrencyKey] ?: "COP" }

    override val useCustomExchangeRates: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[useCustomExchangeRatesKey] ?: false }

    override val customUsdRate: Flow<Float> = context.dataStore.data
        .map { prefs -> prefs[customUsdRateKey] ?: 0f }

    override val customEurRate: Flow<Float> = context.dataStore.data
        .map { prefs -> prefs[customEurRateKey] ?: 0f }

    override val customGbpRate: Flow<Float> = context.dataStore.data
        .map { prefs -> prefs[customGbpRateKey] ?: 0f }

    override val investmentSortField: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[investmentSortFieldKey] ?: "ALPHABETICAL" }

    override val investmentSortDirection: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[investmentSortDirectionKey] ?: "ASC" }

    override val homeSortField: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[homeSortFieldKey] ?: "ALPHABETICAL" }

    override val homeSortDirection: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[homeSortDirectionKey] ?: "ASC" }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[notificationsEnabledKey] = enabled }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[themeModeKey] = when (mode) {
                ThemeMode.SYSTEM -> "system"
                ThemeMode.LIGHT -> "light"
                ThemeMode.DARK -> "dark"
            }
        }
    }

    override suspend fun setDefaultCurrency(currency: String) {
        context.dataStore.edit { prefs -> prefs[defaultCurrencyKey] = currency }
    }

    override suspend fun setUseCustomExchangeRates(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[useCustomExchangeRatesKey] = enabled }
    }

    override suspend fun setCustomRates(usd: Float, eur: Float, gbp: Float) {
        context.dataStore.edit { prefs ->
            prefs[customUsdRateKey] = usd
            prefs[customEurRateKey] = eur
            prefs[customGbpRateKey] = gbp
        }
    }

    override suspend fun setInvestmentSort(field: String, direction: String) {
        context.dataStore.edit { prefs ->
            prefs[investmentSortFieldKey] = field
            prefs[investmentSortDirectionKey] = direction
        }
    }

    override suspend fun setHomeSort(field: String, direction: String) {
        context.dataStore.edit { prefs ->
            prefs[homeSortFieldKey] = field
            prefs[homeSortDirectionKey] = direction
        }
    }
}
