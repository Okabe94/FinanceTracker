package com.software.financetracker.data.repository

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.software.financetracker.core.error.DataError
import com.software.financetracker.core.error.EmptyResult
import com.software.financetracker.core.error.Result
import com.software.financetracker.data.local.expense.CategoryMonthTotal
import com.software.financetracker.data.local.expense.CategoryMonthlyBreakdown
import com.software.financetracker.data.local.expense.DayOfWeekTotal
import com.software.financetracker.data.local.expense.ExpenseDao
import com.software.financetracker.data.local.expense.ExpenseEntity
import com.software.financetracker.data.local.expense.MonthlyTotal
import com.software.financetracker.data.local.expense.TopExpenseRow
import com.software.financetracker.data.worker.WidgetRefreshWorker
import com.software.financetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

class ExpenseRepositoryImpl(
    private val dao: ExpenseDao,
    private val context: Context
) : ExpenseRepository {

    override fun observeByCategory(categoryId: Long): Flow<List<ExpenseEntity>> =
        dao.observeByCategory(categoryId)

    override fun observeMonthlyTotalsByCategory(yearMonth: String): Flow<List<CategoryMonthTotal>> {
        val ym = YearMonth.parse(yearMonth)
        val startDate = ym.atDay(1).toString()
        val endDate = ym.atEndOfMonth().toString()
        return dao.observeMonthlyTotalsByCategory(startDate, endDate)
    }

    override fun observeMonthlyTotals(startDate: String, endDate: String): Flow<List<MonthlyTotal>> =
        dao.observeMonthlyTotals(startDate, endDate)

    override fun observeCategoryMonthlyBreakdown(startDate: String, endDate: String): Flow<List<CategoryMonthlyBreakdown>> =
        dao.observeCategoryMonthlyBreakdown(startDate, endDate)

    override fun observeTopExpenses(yearMonth: String, limit: Int): Flow<List<TopExpenseRow>> {
        val ym = YearMonth.parse(yearMonth)
        return dao.observeTopExpenses(ym.atDay(1).toString(), ym.atEndOfMonth().toString(), limit)
    }

    override fun observeSpendByDayOfWeek(startDate: String, endDate: String): Flow<List<DayOfWeekTotal>> =
        dao.observeSpendByDayOfWeek(startDate, endDate)

    override suspend fun getAllInRange(startDate: String, endDate: String): Result<List<TopExpenseRow>, DataError.Local> =
        try {
            Result.Success(dao.getAllInRange(startDate, endDate))
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    override suspend fun getById(id: Long): Result<ExpenseEntity, DataError.Local> =
        try {
            val entity = dao.getById(id)
            if (entity != null) Result.Success(entity) else Result.Error(DataError.Local.NOT_FOUND)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    override suspend fun upsert(entity: ExpenseEntity): Result<Long, DataError.Local> =
        try {
            val id = if (entity.id == 0L) dao.insert(entity) else { dao.update(entity); entity.id }
            enqueueWidgetRefresh()
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    override suspend fun insertAll(entities: List<ExpenseEntity>): EmptyResult<DataError.Local> =
        try {
            dao.insertAll(entities)
            enqueueWidgetRefresh()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    override suspend fun delete(entity: ExpenseEntity): EmptyResult<DataError.Local> =
        try {
            dao.delete(entity)
            enqueueWidgetRefresh()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    private fun enqueueWidgetRefresh() {
        WorkManager.getInstance(context).enqueueUniqueWork(
            "widget_refresh",
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<WidgetRefreshWorker>().build()
        )
    }
}
