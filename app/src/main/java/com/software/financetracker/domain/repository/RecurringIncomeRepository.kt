package com.software.financetracker.domain.repository

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.income.RecurringIncomeEntity
import kotlinx.coroutines.flow.Flow

interface RecurringIncomeRepository {
    fun observeActive(): Flow<List<RecurringIncomeEntity>>
    suspend fun getById(id: Long): Result<RecurringIncomeEntity, DataError.Local>
    suspend fun upsert(entity: RecurringIncomeEntity): Result<Long, DataError.Local>
    suspend fun delete(entity: RecurringIncomeEntity): EmptyResult<DataError.Local>
}
