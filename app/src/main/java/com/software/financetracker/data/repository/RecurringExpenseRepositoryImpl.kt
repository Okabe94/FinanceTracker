package com.software.financetracker.data.repository

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.recurring.RecurringExpenseDao
import com.software.financetracker.data.local.recurring.RecurringExpenseEntity
import com.software.financetracker.domain.repository.RecurringExpenseRepository
import kotlinx.coroutines.flow.Flow

class RecurringExpenseRepositoryImpl(
    private val dao: RecurringExpenseDao
) : RecurringExpenseRepository {

    override fun observeActive(): Flow<List<RecurringExpenseEntity>> = dao.observeActive()

    override fun observeByCategory(categoryId: Long): Flow<List<RecurringExpenseEntity>> =
        dao.observeByCategory(categoryId)

    override suspend fun getById(id: Long): Result<RecurringExpenseEntity, DataError.Local> =
        try {
            val entity = dao.getById(id)
            if (entity != null) Result.Success(entity) else Result.Error(DataError.Local.NOT_FOUND)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    override suspend fun upsert(entity: RecurringExpenseEntity): Result<Long, DataError.Local> =
        try {
            val id = if (entity.id == 0L) dao.insert(entity) else { dao.update(entity); entity.id }
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    override suspend fun delete(entity: RecurringExpenseEntity): EmptyResult<DataError.Local> =
        try {
            dao.delete(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
}
