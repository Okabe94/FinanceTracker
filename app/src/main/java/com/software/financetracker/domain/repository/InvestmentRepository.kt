package com.software.financetracker.domain.repository

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.investment.InvestmentEntity
import kotlinx.coroutines.flow.Flow

interface InvestmentRepository {
    fun observeAll(): Flow<List<InvestmentEntity>>
    suspend fun getById(id: Long): Result<InvestmentEntity, DataError.Local>
    suspend fun upsert(entity: InvestmentEntity): Result<Long, DataError.Local>
    suspend fun delete(entity: InvestmentEntity): EmptyResult<DataError.Local>
}
