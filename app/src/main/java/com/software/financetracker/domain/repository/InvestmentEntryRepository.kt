package com.software.financetracker.domain.repository

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.investment.InvestmentEntryEntity
import kotlinx.coroutines.flow.Flow

interface InvestmentEntryRepository {
    fun observeByInvestment(investmentId: Long): Flow<List<InvestmentEntryEntity>>
    suspend fun getAllByInvestmentAsc(investmentId: Long): List<InvestmentEntryEntity>
    suspend fun getById(id: Long): Result<InvestmentEntryEntity, DataError.Local>
    suspend fun upsert(entity: InvestmentEntryEntity): Result<Long, DataError.Local>
    suspend fun insertAll(entities: List<InvestmentEntryEntity>): EmptyResult<DataError.Local>
    suspend fun delete(entity: InvestmentEntryEntity): EmptyResult<DataError.Local>
}
