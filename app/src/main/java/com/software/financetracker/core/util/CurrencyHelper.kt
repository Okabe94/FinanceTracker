package com.software.financetracker.core.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyHelper {

    private val NO_SUBUNIT_CURRENCIES = setOf("COP")

    val supportedCurrencies = listOf("COP", "USD", "EUR", "GBP")

    fun minorUnitsPerMajor(currency: String): Int =
        if (currency in NO_SUBUNIT_CURRENCIES) 1 else 100

    fun format(amountMinorUnits: Long, currency: String): String {
        val factor = minorUnitsPerMajor(currency)
        return when (currency) {
            "COP" -> "$ " + NumberFormat.getNumberInstance(Locale("es", "CO")).format(amountMinorUnits)
            "USD" -> "$ " + String.format(Locale.US, "%.2f", amountMinorUnits.toDouble() / factor)
            "EUR" -> "€ " + String.format(Locale("es", "ES"), "%.2f", amountMinorUnits.toDouble() / factor)
            "GBP" -> "£ " + String.format(Locale.UK, "%.2f", amountMinorUnits.toDouble() / factor)
            else  -> "$currency " + String.format(Locale.US, "%.2f", amountMinorUnits.toDouble() / factor)
        }
    }

    fun formatAbbr(amountMinorUnits: Long, currency: String): String {
        val factor = minorUnitsPerMajor(currency)
        val major = amountMinorUnits.toDouble() / factor
        return when {
            major >= 1_000_000 -> String.format(Locale.US, "%.1fM", major / 1_000_000)
            major >= 1_000     -> String.format(Locale.US, "%.0fK", major / 1_000)
            else               -> String.format(Locale.US, "%.0f", major)
        }
    }

    fun parseInput(input: String, currency: String): Long? {
        val cleaned = input.replace(",", ".").trim()
        return if (currency in NO_SUBUNIT_CURRENCIES) {
            cleaned.toLongOrNull()
        } else {
            val d = cleaned.toDoubleOrNull() ?: return null
            (d * minorUnitsPerMajor(currency)).toLong()
        }
    }

    fun toInputString(amountMinorUnits: Long, currency: String): String {
        val factor = minorUnitsPerMajor(currency)
        return if (factor == 1) amountMinorUnits.toString()
        else String.format(Locale.US, "%.2f", amountMinorUnits.toDouble() / factor)
    }

    fun currencySymbol(currency: String): String = when (currency) {
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        else  -> "$"
    }

    fun convertToCop(amountMinorUnits: Long, fromCurrency: String, rates: Map<String, Double>): Long? {
        if (fromCurrency == "COP") return amountMinorUnits
        val copPerUnit = rates[fromCurrency] ?: return null
        val major = amountMinorUnits.toDouble() / minorUnitsPerMajor(fromCurrency)
        return (major * copPerUnit).toLong()
    }
}
