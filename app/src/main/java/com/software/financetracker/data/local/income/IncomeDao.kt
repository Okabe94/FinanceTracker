package com.software.financetracker.data.local.income

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Query("SELECT * FROM income WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun observeInRange(startDate: String, endDate: String): Flow<List<IncomeEntity>>

    @Query("""SELECT substr(date,1,7) AS yearMonth, SUM(amountCop) AS total FROM income WHERE date >= :startDate AND date <= :endDate GROUP BY yearMonth ORDER BY yearMonth ASC""")
    fun observeMonthlyTotals(startDate: String, endDate: String): Flow<List<IncomeMonthlyTotal>>

    @Query("SELECT SUM(amountCop) FROM income WHERE date >= :startDate AND date <= :endDate")
    fun observeTotalInRange(startDate: String, endDate: String): Flow<Long?>

    @Insert
    suspend fun insert(entity: IncomeEntity): Long

    @Update
    suspend fun update(entity: IncomeEntity)

    @Delete
    suspend fun delete(entity: IncomeEntity)

    @Query("SELECT * FROM income WHERE id = :id")
    suspend fun getById(id: Long): IncomeEntity?
}
