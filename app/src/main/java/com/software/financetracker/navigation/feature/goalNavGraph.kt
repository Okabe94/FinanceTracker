package com.software.financetracker.navigation.feature

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.software.financetracker.feature.goal.detail.GoalDetailScreenRoot
import com.software.financetracker.feature.goal.form.GoalFormScreenRoot
import com.software.financetracker.feature.goal.list.GoalListScreenRoot
import com.software.financetracker.navigation.GoalDetailRoute
import com.software.financetracker.navigation.GoalFormRoute
import com.software.financetracker.navigation.GoalListRoute

fun NavGraphBuilder.goalNavGraph(navController: NavController) {
    composable<GoalListRoute> { GoalListScreenRoot(navController) }
    composable<GoalFormRoute> { GoalFormScreenRoot(navController) }
    composable<GoalDetailRoute> { GoalDetailScreenRoot(navController) }
}
