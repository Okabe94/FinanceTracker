package com.software.financetracker

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.software.financetracker.core.notification.NotificationHelper
import com.software.financetracker.data.di.dataModule
import com.software.financetracker.data.worker.BudgetCheckWorker
import com.software.financetracker.data.worker.RecurringExpenseWorker
import com.software.financetracker.feature.category.di.categoryModule
import com.software.financetracker.feature.expense.di.expenseModule
import com.software.financetracker.feature.home.di.homeModule
import com.software.financetracker.feature.metrics.di.metricsModule
import com.software.financetracker.feature.investment.di.investmentModule
import com.software.financetracker.feature.recurring.di.recurringModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class FinanceTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        NotificationHelper.createChannel(this)

        startKoin {
            androidLogger()
            androidContext(this@FinanceTrackerApp)
            modules(dataModule, homeModule, categoryModule, expenseModule, metricsModule, recurringModule, investmentModule)
        }

        val budgetCheckRequest = PeriodicWorkRequestBuilder<BudgetCheckWorker>(
            4, TimeUnit.HOURS,
            1, TimeUnit.HOURS
        ).build()

        val recurringExpenseRequest = PeriodicWorkRequestBuilder<RecurringExpenseWorker>(
            24, TimeUnit.HOURS,
            1, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).apply {
            enqueueUniquePeriodicWork(
                "budget_check",
                ExistingPeriodicWorkPolicy.KEEP,
                budgetCheckRequest
            )
            enqueueUniquePeriodicWork(
                "recurring_expense_check",
                ExistingPeriodicWorkPolicy.KEEP,
                recurringExpenseRequest
            )
        }
    }
}
