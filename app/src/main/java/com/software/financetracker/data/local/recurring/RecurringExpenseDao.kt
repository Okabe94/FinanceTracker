package com.software.financetracker.data.local.recurring

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringExpenseDao {
    @Query("SELECT * FROM recurring_expenses WHERE isActive = 1 ORDER BY nextDueDate ASC")
    fun observeActive(): Flow<List<RecurringExpenseEntity>>

    @Query("SELECT * FROM recurring_expenses WHERE isActive = 1 AND nextDueDate <= :today")
    suspend fun getDueToday(today: String): List<RecurringExpenseEntity>

    @Query("SELECT * FROM recurring_expenses WHERE categoryId = :categoryId ORDER BY nextDueDate ASC")
    fun observeByCategory(categoryId: Long): Flow<List<RecurringExpenseEntity>>

    @Query("SELECT * FROM recurring_expenses WHERE id = :id")
    suspend fun getById(id: Long): RecurringExpenseEntity?

    @Insert
    suspend fun insert(entity: RecurringExpenseEntity): Long

    @Update
    suspend fun update(entity: RecurringExpenseEntity)

    @Delete
    suspend fun delete(entity: RecurringExpenseEntity)
}
