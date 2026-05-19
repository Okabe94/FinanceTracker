package com.software.financetracker.domain.repository

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.recurring.RecurringExpenseEntity
import kotlinx.coroutines.flow.Flow

interface RecurringExpenseRepository {
    fun observeActive(): Flow<List<RecurringExpenseEntity>>
    fun observeByCategory(categoryId: Long): Flow<List<RecurringExpenseEntity>>
    suspend fun getById(id: Long): Result<RecurringExpenseEntity, DataError.Local>
    suspend fun upsert(entity: RecurringExpenseEntity): Result<Long, DataError.Local>
    suspend fun delete(entity: RecurringExpenseEntity): EmptyResult<DataError.Local>
}
