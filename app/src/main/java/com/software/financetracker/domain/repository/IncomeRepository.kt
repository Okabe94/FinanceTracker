package com.software.financetracker.domain.repository

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.income.IncomeEntity
import kotlinx.coroutines.flow.Flow

interface IncomeRepository {
    fun observeInRange(startDate: String, endDate: String): Flow<List<IncomeEntity>>
    fun observeTotalInRange(startDate: String, endDate: String): Flow<Long?>
    suspend fun getById(id: Long): Result<IncomeEntity, DataError.Local>
    suspend fun upsert(entity: IncomeEntity): Result<Long, DataError.Local>
    suspend fun delete(entity: IncomeEntity): EmptyResult<DataError.Local>
}
