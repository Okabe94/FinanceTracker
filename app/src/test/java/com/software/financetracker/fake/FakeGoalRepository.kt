package com.software.financetracker.fake

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.goal.GoalEntity
import com.software.financetracker.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeGoalRepository : GoalRepository {
    var shouldReturnError = false
    private val goals = mutableListOf<GoalEntity>()
    private val _flow = MutableStateFlow<List<GoalEntity>>(emptyList())

    fun seed(vararg entities: GoalEntity) {
        goals.addAll(entities)
        _flow.value = goals.toList()
    }

    override fun observeActive(): Flow<List<GoalEntity>> =
        _flow.map { list -> list.filter { !it.isAchieved }.sortedBy { it.deadlineDate } }

    override fun observeAchieved(): Flow<List<GoalEntity>> =
        _flow.map { list -> list.filter { it.isAchieved }.sortedByDescending { it.deadlineDate } }

    override suspend fun getById(id: Long): Result<GoalEntity, DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        val found = goals.find { it.id == id }
        return if (found != null) Result.Success(found) else Result.Error(DataError.Local.NOT_FOUND)
    }

    override suspend fun upsert(entity: GoalEntity): Result<Long, DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        val existing = goals.indexOfFirst { it.id == entity.id && entity.id != 0L }
        val assignedId: Long
        if (existing >= 0) {
            goals[existing] = entity
            assignedId = entity.id
        } else {
            val newId = (goals.maxOfOrNull { it.id } ?: 0L) + 1L
            goals.add(entity.copy(id = newId))
            assignedId = newId
        }
        _flow.value = goals.toList()
        return Result.Success(assignedId)
    }

    override suspend fun delete(entity: GoalEntity): EmptyResult<DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        goals.removeAll { it.id == entity.id }
        _flow.value = goals.toList()
        return Result.Success(Unit)
    }
}
