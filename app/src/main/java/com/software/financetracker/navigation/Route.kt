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
