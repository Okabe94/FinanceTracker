package com.software.financetracker.domain.repository

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.category.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeAll(): Flow<List<CategoryEntity>>
    fun observeById(id: Long): Flow<Result<CategoryEntity, DataError.Local>>
    suspend fun getById(id: Long): Result<CategoryEntity, DataError.Local>
    suspend fun upsert(entity: CategoryEntity): Result<Long, DataError.Local>
    suspend fun delete(entity: CategoryEntity): EmptyResult<DataError.Local>
}
