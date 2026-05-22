package com.software.financetracker.fake

import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.expense.CategoryMonthTotal
import com.software.financetracker.data.local.expense.CategoryMonthlyBreakdown
import com.software.financetracker.data.local.expense.DayOfWeekTotal
import com.software.financetracker.data.local.expense.ExpenseEntity
import com.software.financetracker.data.local.expense.MonthlyTotal
import com.software.financetracker.data.local.expense.TopExpenseRow
import com.software.financetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeExpenseRepository : ExpenseRepository {

    var shouldReturnError = false

    private val expenses = mutableListOf<ExpenseEntity>()
    private var nextId = 1L

    fun seed(vararg entities: ExpenseEntity) {
        expenses.addAll(entities)
        if (entities.isNotEmpty()) {
            nextId = (expenses.maxOf { it.id }) + 1L
        }
    }

    override fun observeByCategory(categoryId: Long): Flow<List<ExpenseEntity>> =
        flowOf(emptyList())

    override fun observeMonthlyTotalsByCategory(yearMonth: String): Flow<List<CategoryMonthTotal>> =
        flowOf(emptyList())

    override fun observeMonthlyTotals(startDate: String, endDate: String): Flow<List<MonthlyTotal>> =
        flowOf(emptyList())

    override fun observeCategoryMonthlyBreakdown(
        startDate: String,
        endDate: String
    ): Flow<List<CategoryMonthlyBreakdown>> = flowOf(emptyList())

    override fun observeTopExpenses(yearMonth: String, limit: Int): Flow<List<TopExpenseRow>> =
        flowOf(emptyList())

    override fun observeSpendByDayOfWeek(
        startDate: String,
        endDate: String
    ): Flow<List<DayOfWeekTotal>> = flowOf(emptyList())

    override suspend fun getAllInRange(
        startDate: String,
        endDate: String
    ): Result<List<TopExpenseRow>, DataError.Local> =
        Result.Success(emptyList())

    override suspend fun getById(id: Long): Result<ExpenseEntity, DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        val found = expenses.find { it.id == id }
        return if (found != null) Result.Success(found)
        else Result.Error(DataError.Local.NOT_FOUND)
    }

    override suspend fun upsert(entity: ExpenseEntity): Result<Long, DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        val existing = expenses.indexOfFirst { it.id == entity.id && entity.id != 0L }
        val assignedId: Long
        if (existing >= 0) {
            expenses[existing] = entity
            assignedId = entity.id
        } else {
            assignedId = nextId++
            expenses.add(entity.copy(id = assignedId))
        }
        return Result.Success(assignedId)
    }

    override suspend fun delete(entity: ExpenseEntity): EmptyResult<DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        expenses.removeAll { it.id == entity.id }
        return Result.Success(Unit)
    }
}
