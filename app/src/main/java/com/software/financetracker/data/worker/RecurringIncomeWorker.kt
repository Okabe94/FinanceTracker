package com.software.financetracker.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.data.local.income.IncomeDao
import com.software.financetracker.data.local.income.IncomeEntity
import com.software.financetracker.data.local.income.RecurringIncomeDao
import com.software.financetracker.data.local.income.RecurringIncomeEntity
import com.software.financetracker.domain.model.RecurrenceType
import com.software.financetracker.domain.model.toRecurrenceType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate

class RecurringIncomeWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val recurringIncomeDao: RecurringIncomeDao by inject()
    private val incomeDao: IncomeDao by inject()

    override suspend fun doWork(): Result {
        val today = DateUtil.today()
        val todayDate = LocalDate.parse(today)
        val dueTemplates = recurringIncomeDao.getDueToday(today)

        for (template in dueTemplates) {
            generateInstancesForTemplate(template, todayDate)
        }

        return Result.success()
    }

    private suspend fun generateInstancesForTemplate(
        template: RecurringIncomeEntity,
        today: LocalDate
    ) {
        var current = template
        var iterations = 0

        while (iterations < 60) {
            val nextDue = LocalDate.parse(current.nextDueDate)
            if (nextDue > today) break

            incomeDao.insert(
                IncomeEntity(
                    amountCop = current.amountCop,
                    source = current.source,
                    date = current.nextDueDate,
                    notes = current.notes,
                    recurringIncomeId = current.id
                )
            )

            val advancedDate = advanceDate(nextDue, current.recurrenceType.toRecurrenceType())
            current = current.copy(nextDueDate = advancedDate.toString())
            iterations++
        }

        if (current.nextDueDate != template.nextDueDate) {
            recurringIncomeDao.update(current)
        }
    }

    // Monthly recurrence applied to Jan 31 → Feb 28/29; Feb 28 → Mar 28, not Mar 31.
    private fun advanceDate(date: LocalDate, recurrenceType: RecurrenceType): LocalDate =
        when (recurrenceType) {
            RecurrenceType.Daily -> date.plusDays(1)
            RecurrenceType.Weekly -> date.plusDays(7)
            RecurrenceType.Biweekly -> date.plusDays(14)
            RecurrenceType.Monthly -> date.plusMonths(1)
            is RecurrenceType.Custom -> date.plusDays(recurrenceType.intervalDays.toLong())
        }
}
