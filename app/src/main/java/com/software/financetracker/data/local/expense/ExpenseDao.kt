package com.software.financetracker.data.local.expense

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE categoryId = :categoryId ORDER BY date DESC")
    fun observeByCategory(categoryId: Long): Flow<List<ExpenseEntity>>

    @Query("""
        SELECT categoryId, SUM(amountCop) as total
        FROM expenses
        WHERE date >= :startDate AND date <= :endDate
        GROUP BY categoryId
    """)
    fun observeMonthlyTotalsByCategory(
        startDate: String,
        endDate: String
    ): Flow<List<CategoryMonthTotal>>

    @Query("""
        SELECT categoryId, SUM(amountCop) as total
        FROM expenses
        WHERE date >= :startDate AND date <= :endDate
        GROUP BY categoryId
    """)
    suspend fun getMonthlyTotalsByCategory(startDate: String, endDate: String): List<CategoryMonthTotal>

    @Query("""
        SELECT substr(date, 1, 7) AS yearMonth, SUM(amountCop) AS total
        FROM expenses
        WHERE date >= :startDate AND date <= :endDate
        GROUP BY substr(date, 1, 7)
        ORDER BY yearMonth ASC
    """)
    fun observeMonthlyTotals(startDate: String, endDate: String): Flow<List<MonthlyTotal>>

    @Query("""
        SELECT substr(date, 1, 7) AS yearMonth, categoryId, SUM(amountCop) AS total
        FROM expenses
        WHERE date >= :startDate AND date <= :endDate
        GROUP BY substr(date, 1, 7), categoryId
        ORDER BY yearMonth ASC, categoryId ASC
    """)
    fun observeCategoryMonthlyBreakdown(startDate: String, endDate: String): Flow<List<CategoryMonthlyBreakdown>>

    @Query("""
        SELECT e.id, e.categoryId, e.amountCop, e.description, e.date,
               c.name AS categoryName, c.colorArgb
        FROM expenses e
        JOIN categories c ON e.categoryId = c.id
        WHERE e.date >= :startDate AND e.date <= :endDate
        ORDER BY e.amountCop DESC
        LIMIT :limit
    """)
    fun observeTopExpenses(startDate: String, endDate: String, limit: Int): Flow<List<TopExpenseRow>>

    @Query("""
        SELECT CAST(strftime('%w', date) AS INTEGER) AS dayOfWeek,
               SUM(amountCop) AS total
        FROM expenses
        WHERE date >= :startDate AND date <= :endDate
        GROUP BY dayOfWeek
        ORDER BY dayOfWeek ASC
    """)
    fun observeSpendByDayOfWeek(startDate: String, endDate: String): Flow<List<DayOfWeekTotal>>

    @Query("""
        SELECT e.id, e.categoryId, e.amountCop, e.description, e.date,
               c.name AS categoryName, c.colorArgb
        FROM expenses e
        JOIN categories c ON e.categoryId = c.id
        WHERE e.date >= :startDate AND e.date <= :endDate
        ORDER BY e.date ASC
    """)
    suspend fun getAllInRange(startDate: String, endDate: String): List<TopExpenseRow>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: Long): ExpenseEntity?

    @Query("SELECT * FROM expenses")
    suspend fun getAll(): List<ExpenseEntity>

    @Insert
    suspend fun insert(entity: ExpenseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ExpenseEntity>)

    @Update
    suspend fun update(entity: ExpenseEntity)

    @Delete
    suspend fun delete(entity: ExpenseEntity)

    @Query("DELETE FROM expenses")
    suspend fun deleteAll()
}
