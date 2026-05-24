package com.software.financetracker.domain.repository

import com.software.financetracker.data.local.investment.ExchangeRateEntity
import kotlinx.coroutines.flow.Flow

interface ExchangeRateRepository {
    fun getAll(): Flow<List<ExchangeRateEntity>>
    suspend fun refresh()
    suspend fun upsert(entity: ExchangeRateEntity)
}
