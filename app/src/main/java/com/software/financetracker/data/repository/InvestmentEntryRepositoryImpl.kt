package com.software.financetracker.data.repository

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.investment.InvestmentEntryDao
import com.software.financetracker.data.local.investment.InvestmentEntryEntity
import com.software.financetracker.domain.repository.InvestmentEntryRepository
import kotlinx.coroutines.flow.Flow

class InvestmentEntryRepositoryImpl(
    private val dao: InvestmentEntryDao
) : InvestmentEntryRepository {

    override fun observeByInvestment(investmentId: Long): Flow<List<InvestmentEntryEntity>> =
        dao.observeByInvestment(investmentId)

    override suspend fun getAllByInvestmentAsc(investmentId: Long): List<InvestmentEntryEntity> =
        dao.getAllByInvestmentAsc(investmentId)

    override suspend fun getById(id: Long): Result<InvestmentEntryEntity, DataError.Local> =
        try {
            val entity = dao.getById(id)
            if (entity != null) Result.Success(entity) else Result.Error(DataError.Local.NOT_FOUND)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    override suspend fun upsert(entity: InvestmentEntryEntity): Result<Long, DataError.Local> =
        try {
            val id = if (entity.id == 0L) dao.insert(entity) else { dao.update(entity); entity.id }
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    override suspend fun delete(entity: InvestmentEntryEntity): EmptyResult<DataError.Local> =
        try {
            dao.delete(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
}
