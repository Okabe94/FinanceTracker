package com.software.financetracker.fake

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.investment.InvestmentEntity
import com.software.financetracker.domain.repository.InvestmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeInvestmentRepository : InvestmentRepository {

    var shouldReturnError = false

    private val investments = mutableListOf<InvestmentEntity>()
    private val _flow = MutableStateFlow<List<InvestmentEntity>>(emptyList())

    fun seed(vararg entities: InvestmentEntity) {
        investments.addAll(entities)
        _flow.value = investments.toList()
    }

    override fun observeAll(): Flow<List<InvestmentEntity>> = _flow

    override suspend fun getById(id: Long): Result<InvestmentEntity, DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        val found = investments.find { it.id == id }
        return if (found != null) Result.Success(found)
        else Result.Error(DataError.Local.NOT_FOUND)
    }

    override suspend fun upsert(entity: InvestmentEntity): Result<Long, DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        val existing = investments.indexOfFirst { it.id == entity.id && entity.id != 0L }
        val assignedId: Long
        if (existing >= 0) {
            investments[existing] = entity
            assignedId = entity.id
        } else {
            val newId = (investments.maxOfOrNull { it.id } ?: 0L) + 1L
            investments.add(entity.copy(id = newId))
            assignedId = newId
        }
        _flow.value = investments.toList()
        return Result.Success(assignedId)
    }

    override suspend fun delete(entity: InvestmentEntity): EmptyResult<DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        investments.removeAll { it.id == entity.id }
        _flow.value = investments.toList()
        return Result.Success(Unit)
    }
}
