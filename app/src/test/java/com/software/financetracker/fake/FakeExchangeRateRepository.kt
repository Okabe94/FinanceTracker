package com.software.financetracker.fake

import com.software.financetracker.data.local.investment.ExchangeRateEntity
import com.software.financetracker.domain.repository.ExchangeRateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeExchangeRateRepository : ExchangeRateRepository {

    private val _flow = MutableStateFlow<List<ExchangeRateEntity>>(emptyList())

    fun seed(vararg entities: ExchangeRateEntity) {
        _flow.value = entities.toList()
    }

    override fun getAll(): Flow<List<ExchangeRateEntity>> = _flow

    override suspend fun refresh() {}
}
