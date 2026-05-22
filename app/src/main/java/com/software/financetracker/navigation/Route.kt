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
