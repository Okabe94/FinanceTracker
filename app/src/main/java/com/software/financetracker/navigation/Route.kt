package com.software.financetracker.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
data class CategoryDetailRoute(val categoryId: Long, val selectedMonth: String)

@Serializable
data class CategoryFormRoute(val categoryId: Long? = null)

@Serializable
data class ExpenseFormRoute(val categoryId: Long, val expenseId: Long? = null)

@Serializable
object MetricsRoute

@Serializable
object RecurringListRoute

@Serializable
data class RecurringExpenseFormRoute(
    val categoryId: Long = 0L,
    val recurringExpenseId: Long? = null
)

@Serializable
object InvestmentListRoute

@Serializable
data class InvestmentDetailRoute(val investmentId: Long)

@Serializable
data class InvestmentFormRoute(val investmentId: Long? = null)

@Serializable
data class InvestmentEntryFormRoute(
    val investmentId: Long,
    val entryId: Long? = null
)

@Serializable
data class AssistantExpenseRoute(
    val categoryName: String = "",
    val amountCop: Long = 0L
)

@Serializable
data class IncomeFormRoute(val incomeId: Long? = null)

@Serializable
object IncomeListRoute

@Serializable
object RecurringIncomeListRoute

@Serializable
data class RecurringIncomeFormRoute(val recurringIncomeId: Long? = null)

@Serializable
object GoalListRoute

@Serializable
data class GoalDetailRoute(val goalId: Long)

@Serializable
data class GoalFormRoute(val goalId: Long? = null)
