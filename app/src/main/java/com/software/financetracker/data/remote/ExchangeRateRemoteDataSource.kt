package com.software.financetracker.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ExchangeRateRemoteDataSource {
    suspend fun fetchCopPerUnit(): Map<String, Double> = withContext(Dispatchers.IO) {
        val conn = URL("https://open.er-api.com/v6/latest/USD").openConnection() as HttpURLConnection
        conn.connectTimeout = 8_000
        conn.readTimeout = 8_000
        try {
            val json = JSONObject(conn.inputStream.bufferedReader().readText())
            val rates = json.getJSONObject("rates")
            val copPerUsd = rates.getDouble("COP")
            mapOf(
                "USD" to copPerUsd,
                "EUR" to copPerUsd / rates.getDouble("EUR"),
                "GBP" to copPerUsd / rates.getDouble("GBP")
            )
        } finally {
            conn.disconnect()
        }
    }
}
