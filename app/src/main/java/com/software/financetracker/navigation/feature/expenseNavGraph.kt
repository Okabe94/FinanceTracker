package com.software.financetracker.navigation.feature

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.software.financetracker.feature.expense.assistant.AssistantExpenseScreenRoot
import com.software.financetracker.feature.expense.batch.BatchExpenseScreenRoot
import com.software.financetracker.feature.expense.form.ExpenseFormScreenRoot
import com.software.financetracker.navigation.AssistantExpenseRoute
import com.software.financetracker.navigation.BatchExpenseRoute
import com.software.financetracker.navigation.ExpenseFormRoute

fun NavGraphBuilder.expenseNavGraph(navController: NavController) {
    composable<ExpenseFormRoute> { ExpenseFormScreenRoot(navController) }
    composable<BatchExpenseRoute> { BatchExpenseScreenRoot(navController) }
    composable<AssistantExpenseRoute>(
        deepLinks = listOf(
            navDeepLink { uriPattern = "financetracker://add-expense?categoryName={categoryName}&amountCop={amountCop}" },
            navDeepLink { uriPattern = "financetracker://add-expense?categoryName={categoryName}" },
            navDeepLink { uriPattern = "financetracker://add-expense" }
        )
    ) {
        AssistantExpenseScreenRoot(navController)
    }
}
