package com.software.financetracker.fake

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.income.IncomeEntity
import com.software.financetracker.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeIncomeRepository : IncomeRepository {
    var shouldReturnError = false
    private val entries = mutableListOf<IncomeEntity>()
    private val _flow = MutableStateFlow<List<IncomeEntity>>(emptyList())

    fun seed(vararg entities: IncomeEntity) {
        entries.addAll(entities)
        _flow.value = entries.toList()
    }

    override fun observeInRange(startDate: String, endDate: String): Flow<List<IncomeEntity>> =
        _flow.map { list -> list.filter { it.date >= startDate && it.date <= endDate } }

    override fun observeTotalInRange(startDate: String, endDate: String): Flow<Long?> =
        _flow.map { list ->
            list.filter { it.date >= startDate && it.date <= endDate }
                .sumOf { it.amountCop }.takeIf { it > 0L }
        }

    override suspend fun getById(id: Long): Result<IncomeEntity, DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        val found = entries.find { it.id == id }
        return if (found != null) Result.Success(found) else Result.Error(DataError.Local.NOT_FOUND)
    }

    override suspend fun upsert(entity: IncomeEntity): Result<Long, DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        val existing = entries.indexOfFirst { it.id == entity.id && entity.id != 0L }
        val assignedId: Long
        if (existing >= 0) {
            entries[existing] = entity
            assignedId = entity.id
        } else {
            val newId = (entries.maxOfOrNull { it.id } ?: 0L) + 1L
            entries.add(entity.copy(id = newId))
            assignedId = newId
        }
        _flow.value = entries.toList()
        return Result.Success(assignedId)
    }

    override suspend fun delete(entity: IncomeEntity): EmptyResult<DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        entries.removeAll { it.id == entity.id }
        _flow.value = entries.toList()
        return Result.Success(Unit)
    }
}
