package com.software.financetracker.domain.repository

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.goal.GoalEntity
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun observeActive(): Flow<List<GoalEntity>>
    fun observeAchieved(): Flow<List<GoalEntity>>
    suspend fun getById(id: Long): Result<GoalEntity, DataError.Local>
    suspend fun upsert(entity: GoalEntity): Result<Long, DataError.Local>
    suspend fun delete(entity: GoalEntity): EmptyResult<DataError.Local>
}
