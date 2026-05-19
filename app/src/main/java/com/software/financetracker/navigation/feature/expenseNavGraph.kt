package com.software.financetracker.navigation.feature

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.software.financetracker.feature.expense.form.ExpenseFormScreenRoot
import com.software.financetracker.navigation.ExpenseFormRoute

fun NavGraphBuilder.expenseNavGraph(navController: NavController) {
    composable<ExpenseFormRoute> { ExpenseFormScreenRoot(navController) }
}
