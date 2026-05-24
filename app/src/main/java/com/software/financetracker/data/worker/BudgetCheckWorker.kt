package com.software.financetracker.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.software.financetracker.core.notification.NotificationHelper
import com.software.financetracker.core.preferences.UserPreferences
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.data.local.category.CategoryDao
import com.software.financetracker.data.local.expense.ExpenseDao
import com.software.financetracker.data.local.notification.NotificationStateDao
import com.software.financetracker.data.local.notification.NotificationStateEntity
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.YearMonth

class BudgetCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val categoryDao: CategoryDao by inject()
    private val expenseDao: ExpenseDao by inject()
    private val notificationStateDao: NotificationStateDao by inject()
    private val userPreferencesRepository: UserPreferences by inject()

    override suspend fun doWork(): Result {
        if (!userPreferencesRepository.notificationsEnabled.first()) return Result.success()

        val currentMonth = DateUtil.currentYearMonth()
        notificationStateDao.clearStaleMonths(currentMonth)

        val ym = YearMonth.parse(currentMonth)
        val startDate = ym.atDay(1).toString()
        val endDate = ym.atEndOfMonth().toString()

        val categoriesWithLimits = categoryDao.getAll().filter { it.monthlyLimitCop != null }
        val totalsByCategory = expenseDao.getMonthlyTotalsByCategory(startDate, endDate)
            .associateBy { it.categoryId }

        for (category in categoriesWithLimits) {
            val limit = category.monthlyLimitCop!!
            val spent = totalsByCategory[category.id]?.total ?: 0L
            val ratio = spent.toDouble() / limit

            val existing = notificationStateDao.getById(category.id)
            val state = when {
                existing == null || existing.forMonth != currentMonth ->
                    NotificationStateEntity(categoryId = category.id, forMonth = currentMonth)
                else -> existing
            }

            var updated = state
            if (ratio >= 0.8 && !state.firedAt80Percent) {
                val percent = (ratio * 100).toInt()
                NotificationHelper.notifyAt80Percent(applicationContext, category.id, category.name, percent)
                updated = updated.copy(firedAt80Percent = true)
            }
            if (ratio >= 1.0 && !state.firedAt100Percent) {
                NotificationHelper.notifyAt100Percent(applicationContext, category.id, category.name)
                updated = updated.copy(firedAt100Percent = true)
            }
            if (updated != state) {
                notificationStateDao.upsert(updated)
            }
        }

        return Result.success()
    }
}
