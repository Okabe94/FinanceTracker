package com.software.financetracker.data.worker

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.software.financetracker.widget.BudgetWidget

class WidgetRefreshWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        BudgetWidget().updateAll(applicationContext)
        return Result.success()
    }
}
