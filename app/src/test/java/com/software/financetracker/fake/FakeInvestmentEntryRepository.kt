package com.software.financetracker.fake

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.investment.InvestmentEntryEntity
import com.software.financetracker.domain.repository.InvestmentEntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeInvestmentEntryRepository : InvestmentEntryRepository {

    var shouldReturnError = false

    private val entries = mutableListOf<InvestmentEntryEntity>()
    private val _flow = MutableStateFlow<List<InvestmentEntryEntity>>(emptyList())

    fun seed(vararg entities: InvestmentEntryEntity) {
        entries.addAll(entities)
        _flow.value = entries.toList()
    }

    override fun observeByInvestment(investmentId: Long): Flow<List<InvestmentEntryEntity>> =
        _flow.map { list -> list.filter { it.investmentId == investmentId } }

    override suspend fun getAllByInvestmentAsc(investmentId: Long): List<InvestmentEntryEntity> =
        entries.filter { it.investmentId == investmentId }
            .sortedWith(compareBy({ it.date }, { it.id }))

    override suspend fun getById(id: Long): Result<InvestmentEntryEntity, DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        val found = entries.find { it.id == id }
        return if (found != null) Result.Success(found)
        else Result.Error(DataError.Local.NOT_FOUND)
    }

    override suspend fun upsert(entity: InvestmentEntryEntity): Result<Long, DataError.Local> {
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

    override suspend fun delete(entity: InvestmentEntryEntity): EmptyResult<DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        entries.removeAll { it.id == entity.id }
        _flow.value = entries.toList()
        return Result.Success(Unit)
    }
}
