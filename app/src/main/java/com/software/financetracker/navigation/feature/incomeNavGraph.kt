package com.software.financetracker.navigation.feature

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.software.financetracker.feature.income.form.IncomeFormScreenRoot
import com.software.financetracker.feature.income.list.IncomeListScreenRoot
import com.software.financetracker.navigation.IncomeFormRoute
import com.software.financetracker.navigation.IncomeListRoute

fun NavGraphBuilder.incomeNavGraph(navController: NavController) {
    composable<IncomeListRoute> { IncomeListScreenRoot(navController) }
    composable<IncomeFormRoute> { IncomeFormScreenRoot(navController) }
}
