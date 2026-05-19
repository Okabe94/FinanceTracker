package com.software.financetracker.domain.repository

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.expense.CategoryMonthTotal
import com.software.financetracker.data.local.expense.CategoryMonthlyBreakdown
import com.software.financetracker.data.local.expense.DayOfWeekTotal
import com.software.financetracker.data.local.expense.ExpenseEntity
import com.software.financetracker.data.local.expense.MonthlyTotal
import com.software.financetracker.data.local.expense.TopExpenseRow
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun observeByCategory(categoryId: Long): Flow<List<ExpenseEntity>>
    fun observeMonthlyTotalsByCategory(yearMonth: String): Flow<List<CategoryMonthTotal>>
    fun observeMonthlyTotals(startDate: String, endDate: String): Flow<List<MonthlyTotal>>
    fun observeCategoryMonthlyBreakdown(startDate: String, endDate: String): Flow<List<CategoryMonthlyBreakdown>>
    fun observeTopExpenses(yearMonth: String, limit: Int): Flow<List<TopExpenseRow>>
    fun observeSpendByDayOfWeek(startDate: String, endDate: String): Flow<List<DayOfWeekTotal>>
    suspend fun getById(id: Long): Result<ExpenseEntity, DataError.Local>
    suspend fun upsert(entity: ExpenseEntity): Result<Long, DataError.Local>
    suspend fun delete(entity: ExpenseEntity): EmptyResult<DataError.Local>
}
