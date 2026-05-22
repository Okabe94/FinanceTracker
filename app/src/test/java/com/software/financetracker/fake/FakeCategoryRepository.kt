package com.software.financetracker.fake

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.category.CategoryEntity
import com.software.financetracker.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeCategoryRepository : CategoryRepository {

    var shouldReturnError = false

    private val categories = mutableListOf<CategoryEntity>()
    private val _flow = MutableStateFlow<List<CategoryEntity>>(emptyList())

    fun seed(vararg entities: CategoryEntity) {
        categories.addAll(entities)
        _flow.value = categories.toList()
    }

    override fun observeAll(): Flow<List<CategoryEntity>> = _flow

    override fun observeById(id: Long): Flow<Result<CategoryEntity, DataError.Local>> =
        _flow.map { list ->
            val found = list.find { it.id == id }
            if (found != null) Result.Success(found)
            else Result.Error(DataError.Local.NOT_FOUND)
        }

    override suspend fun getAll(): List<CategoryEntity> = categories.toList()

    override suspend fun getById(id: Long): Result<CategoryEntity, DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        val found = categories.find { it.id == id }
        return if (found != null) Result.Success(found)
        else Result.Error(DataError.Local.NOT_FOUND)
    }

    override suspend fun upsert(entity: CategoryEntity): Result<Long, DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        val existing = categories.indexOfFirst { it.id == entity.id && entity.id != 0L }
        val assignedId: Long
        if (existing >= 0) {
            categories[existing] = entity
            assignedId = entity.id
        } else {
            val newId = (categories.maxOfOrNull { it.id } ?: 0L) + 1L
            categories.add(entity.copy(id = newId))
            assignedId = newId
        }
        _flow.value = categories.toList()
        return Result.Success(assignedId)
    }

    override suspend fun delete(entity: CategoryEntity): EmptyResult<DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        categories.removeAll { it.id == entity.id }
        _flow.value = categories.toList()
        return Result.Success(Unit)
    }
}
