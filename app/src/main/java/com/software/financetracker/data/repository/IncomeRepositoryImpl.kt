package com.software.financetracker.data.repository

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.income.IncomeDao
import com.software.financetracker.data.local.income.IncomeEntity
import com.software.financetracker.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.Flow

class IncomeRepositoryImpl(private val dao: IncomeDao) : IncomeRepository {
    override fun observeInRange(startDate: String, endDate: String): Flow<List<IncomeEntity>> =
        dao.observeInRange(startDate, endDate)

    override fun observeTotalInRange(startDate: String, endDate: String): Flow<Long?> =
        dao.observeTotalInRange(startDate, endDate)

    override suspend fun getById(id: Long): Result<IncomeEntity, DataError.Local> = try {
        val entity = dao.getById(id)
        if (entity != null) Result.Success(entity) else Result.Error(DataError.Local.NOT_FOUND)
    } catch (e: Exception) { Result.Error(DataError.Local.UNKNOWN) }

    override suspend fun upsert(entity: IncomeEntity): Result<Long, DataError.Local> = try {
        val id = if (entity.id == 0L) dao.insert(entity) else { dao.update(entity); entity.id }
        Result.Success(id)
    } catch (e: Exception) { Result.Error(DataError.Local.UNKNOWN) }

    override suspend fun delete(entity: IncomeEntity): EmptyResult<DataError.Local> = try {
        dao.delete(entity)
        Result.Success(Unit)
    } catch (e: Exception) { Result.Error(DataError.Local.UNKNOWN) }
}
