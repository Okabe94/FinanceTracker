package com.software.financetracker.data.local.income

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringIncomeDao {
    @Query("SELECT * FROM recurring_income WHERE isActive = 1 ORDER BY nextDueDate ASC")
    fun observeActive(): Flow<List<RecurringIncomeEntity>>

    @Query("SELECT * FROM recurring_income WHERE isActive = 1 AND nextDueDate <= :today")
    suspend fun getDueToday(today: String): List<RecurringIncomeEntity>

    @Query("SELECT * FROM recurring_income WHERE id = :id")
    suspend fun getById(id: Long): RecurringIncomeEntity?

    @Query("SELECT * FROM recurring_income")
    suspend fun getAll(): List<RecurringIncomeEntity>

    @Insert
    suspend fun insert(entity: RecurringIncomeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<RecurringIncomeEntity>)

    @Update
    suspend fun update(entity: RecurringIncomeEntity)

    @Delete
    suspend fun delete(entity: RecurringIncomeEntity)

    @Query("DELETE FROM recurring_income")
    suspend fun deleteAll()
}
