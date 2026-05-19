package com.software.financetracker.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.data.local.expense.ExpenseDao
import com.software.financetracker.data.local.expense.ExpenseEntity
import com.software.financetracker.data.local.recurring.RecurringExpenseDao
import com.software.financetracker.data.local.recurring.RecurringExpenseEntity
import com.software.financetracker.domain.model.RecurrenceType
import com.software.financetracker.domain.model.toRecurrenceType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate

class RecurringExpenseWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val recurringExpenseDao: RecurringExpenseDao by inject()
    private val expenseDao: ExpenseDao by inject()

    override suspend fun doWork(): Result {
        val today = DateUtil.today()
        val todayDate = LocalDate.parse(today)
        val dueTemplates = recurringExpenseDao.getDueToday(today)

        for (template in dueTemplates) {
            generateInstancesForTemplate(template, todayDate)
        }

        return Result.success()
    }

    private suspend fun generateInstancesForTemplate(
        template: RecurringExpenseEntity,
        today: LocalDate
    ) {
        var current = template
        var iterations = 0

        while (iterations < 60) {
            val nextDue = LocalDate.parse(current.nextDueDate)
            if (nextDue > today) break

            expenseDao.insert(
                ExpenseEntity(
                    categoryId = current.categoryId,
                    amountCop = current.amountCop,
                    description = current.description,
                    date = current.nextDueDate,
                    recurringExpenseId = current.id
                )
            )

            val advancedDate = advanceDate(nextDue, current.recurrenceType.toRecurrenceType())
            current = current.copy(nextDueDate = advancedDate.toString())
            iterations++
        }

        if (current.nextDueDate != template.nextDueDate) {
            recurringExpenseDao.update(current)
        }
    }

    // Monthly recurrence applied to Jan 31 → Feb 28/29; Feb 28 → Mar 28, not Mar 31.
    // This is standard Java/Kotlin behavior and is intentional for monthly billing patterns.
    private fun advanceDate(date: LocalDate, recurrenceType: RecurrenceType): LocalDate =
        when (recurrenceType) {
            RecurrenceType.Daily -> date.plusDays(1)
            RecurrenceType.Weekly -> date.plusDays(7)
            RecurrenceType.Biweekly -> date.plusDays(14)
            RecurrenceType.Monthly -> date.plusMonths(1)
        }
}
