package com.software.financetracker.data.repository

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.investment.InvestmentDao
import com.software.financetracker.data.local.investment.InvestmentEntity
import com.software.financetracker.domain.repository.InvestmentRepository
import kotlinx.coroutines.flow.Flow

class InvestmentRepositoryImpl(
    private val dao: InvestmentDao
) : InvestmentRepository {

    override fun observeAll(): Flow<List<InvestmentEntity>> = dao.observeAll()

    override suspend fun getById(id: Long): Result<InvestmentEntity, DataError.Local> =
        try {
            val entity = dao.getById(id)
            if (entity != null) Result.Success(entity) else Result.Error(DataError.Local.NOT_FOUND)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    override suspend fun upsert(entity: InvestmentEntity): Result<Long, DataError.Local> =
        try {
            val id = if (entity.id == 0L) dao.insert(entity) else { dao.update(entity); entity.id }
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    override suspend fun delete(entity: InvestmentEntity): EmptyResult<DataError.Local> =
        try {
            dao.delete(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
}
