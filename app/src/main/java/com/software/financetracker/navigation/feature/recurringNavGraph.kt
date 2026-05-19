package com.software.financetracker.navigation.feature

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.software.financetracker.feature.recurring.form.RecurringExpenseFormScreenRoot
import com.software.financetracker.feature.recurring.list.RecurringListScreenRoot
import com.software.financetracker.navigation.RecurringExpenseFormRoute
import com.software.financetracker.navigation.RecurringListRoute

fun NavGraphBuilder.recurringNavGraph(navController: NavController) {
    composable<RecurringListRoute> { RecurringListScreenRoot(navController) }
    composable<RecurringExpenseFormRoute> { RecurringExpenseFormScreenRoot(navController) }
}
