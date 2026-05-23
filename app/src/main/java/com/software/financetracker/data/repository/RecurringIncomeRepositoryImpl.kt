package com.software.financetracker.data.repository

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.income.RecurringIncomeDao
import com.software.financetracker.data.local.income.RecurringIncomeEntity
import com.software.financetracker.domain.repository.RecurringIncomeRepository
import kotlinx.coroutines.flow.Flow

class RecurringIncomeRepositoryImpl(private val dao: RecurringIncomeDao) : RecurringIncomeRepository {

    override fun observeActive(): Flow<List<RecurringIncomeEntity>> = dao.observeActive()

    override suspend fun getById(id: Long): Result<RecurringIncomeEntity, DataError.Local> = try {
        val entity = dao.getById(id)
        if (entity != null) Result.Success(entity) else Result.Error(DataError.Local.NOT_FOUND)
    } catch (e: Exception) { Result.Error(DataError.Local.UNKNOWN) }

    override suspend fun upsert(entity: RecurringIncomeEntity): Result<Long, DataError.Local> = try {
        val id = if (entity.id == 0L) dao.insert(entity) else { dao.update(entity); entity.id }
        Result.Success(id)
    } catch (e: Exception) { Result.Error(DataError.Local.UNKNOWN) }

    override suspend fun delete(entity: RecurringIncomeEntity): EmptyResult<DataError.Local> = try {
        dao.delete(entity)
        Result.Success(Unit)
    } catch (e: Exception) { Result.Error(DataError.Local.UNKNOWN) }
}
