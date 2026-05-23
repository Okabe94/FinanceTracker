package com.software.financetracker.data.repository

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.goal.GoalDao
import com.software.financetracker.data.local.goal.GoalEntity
import com.software.financetracker.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow

class GoalRepositoryImpl(private val dao: GoalDao) : GoalRepository {
    override fun observeActive(): Flow<List<GoalEntity>> = dao.observeActive()
    override fun observeAchieved(): Flow<List<GoalEntity>> = dao.observeAchieved()

    override suspend fun getById(id: Long): Result<GoalEntity, DataError.Local> = try {
        val entity = dao.getById(id)
        if (entity != null) Result.Success(entity) else Result.Error(DataError.Local.NOT_FOUND)
    } catch (e: Exception) { Result.Error(DataError.Local.UNKNOWN) }

    override suspend fun upsert(entity: GoalEntity): Result<Long, DataError.Local> = try {
        val id = if (entity.id == 0L) dao.insert(entity) else { dao.update(entity); entity.id }
        Result.Success(id)
    } catch (e: Exception) { Result.Error(DataError.Local.UNKNOWN) }

    override suspend fun delete(entity: GoalEntity): EmptyResult<DataError.Local> = try {
        dao.delete(entity)
        Result.Success(Unit)
    } catch (e: Exception) { Result.Error(DataError.Local.UNKNOWN) }
}
