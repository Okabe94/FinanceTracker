package com.software.financetracker.fake

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.income.RecurringIncomeEntity
import com.software.financetracker.domain.repository.RecurringIncomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeRecurringIncomeRepository : RecurringIncomeRepository {

    var shouldReturnError = false

    private val recurring = mutableListOf<RecurringIncomeEntity>()
    private val _flow = MutableStateFlow<List<RecurringIncomeEntity>>(emptyList())

    fun seed(vararg entities: RecurringIncomeEntity) {
        recurring.addAll(entities)
        _flow.value = recurring.toList()
    }

    override fun observeActive(): Flow<List<RecurringIncomeEntity>> =
        _flow.map { list -> list.filter { it.isActive } }

    override suspend fun getById(id: Long): Result<RecurringIncomeEntity, DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        val found = recurring.find { it.id == id }
        return if (found != null) Result.Success(found)
        else Result.Error(DataError.Local.NOT_FOUND)
    }

    override suspend fun upsert(entity: RecurringIncomeEntity): Result<Long, DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        val existing = recurring.indexOfFirst { it.id == entity.id && entity.id != 0L }
        val assignedId: Long
        if (existing >= 0) {
            recurring[existing] = entity
            assignedId = entity.id
        } else {
            val newId = (recurring.maxOfOrNull { it.id } ?: 0L) + 1L
            recurring.add(entity.copy(id = newId))
            assignedId = newId
        }
        _flow.value = recurring.toList()
        return Result.Success(assignedId)
    }

    override suspend fun delete(entity: RecurringIncomeEntity): EmptyResult<DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        recurring.removeAll { it.id == entity.id }
        _flow.value = recurring.toList()
        return Result.Success(Unit)
    }
}
