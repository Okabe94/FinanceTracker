package com.software.financetracker.data.repository

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.category.CategoryDao
import com.software.financetracker.data.local.category.CategoryEntity
import com.software.financetracker.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(private val dao: CategoryDao) : CategoryRepository {

    override fun observeAll(): Flow<List<CategoryEntity>> = dao.observeAll()

    override fun observeById(id: Long): Flow<Result<CategoryEntity, DataError.Local>> =
        dao.observeById(id).map { entity ->
            if (entity != null) Result.Success(entity) else Result.Error(DataError.Local.NOT_FOUND)
        }

    override suspend fun getAll(): List<CategoryEntity> = dao.getAll()

    override suspend fun getById(id: Long): Result<CategoryEntity, DataError.Local> =
        try {
            val entity = dao.getById(id)
            if (entity != null) Result.Success(entity) else Result.Error(DataError.Local.NOT_FOUND)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    override suspend fun upsert(entity: CategoryEntity): Result<Long, DataError.Local> =
        try {
            if (entity.id == 0L) {
                Result.Success(dao.insert(entity))
            } else {
                dao.update(entity)
                Result.Success(entity.id)
            }
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    override suspend fun delete(entity: CategoryEntity): EmptyResult<DataError.Local> =
        try {
            dao.delete(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
}
