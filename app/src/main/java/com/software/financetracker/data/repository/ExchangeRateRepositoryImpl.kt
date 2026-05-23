package com.software.financetracker.data.repository

import android.util.Log
import com.software.financetracker.data.local.investment.ExchangeRateDao
import com.software.financetracker.data.local.investment.ExchangeRateEntity
import com.software.financetracker.data.remote.ExchangeRateRemoteDataSource
import com.software.financetracker.domain.repository.ExchangeRateRepository
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ExchangeRateRepositoryImpl(
    private val dao: ExchangeRateDao,
    private val remote: ExchangeRateRemoteDataSource
) : ExchangeRateRepository {

    override fun getAll(): Flow<List<ExchangeRateEntity>> = dao.getAll()

    override suspend fun refresh() {
        try {
            val rates = remote.fetchCopPerUnit()
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            rates.forEach { (from, rate) ->
                dao.upsert(
                    ExchangeRateEntity(
                        fromCurrency = from,
                        toCurrency = "COP",
                        rate = rate,
                        updatedDate = timestamp
                    )
                )
            }
        } catch (e: IOException) {
            Log.d("ExchangeRates", "Fetch failed: ${e.message}")
        } catch (e: Exception) {
            Log.d("ExchangeRates", "Unexpected error: ${e.message}")
        }
    }
}
