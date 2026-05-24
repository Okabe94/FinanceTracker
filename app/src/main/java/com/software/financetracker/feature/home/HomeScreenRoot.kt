package com.software.financetracker.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import com.software.financetracker.navigation.CategoryDetailRoute
import com.software.financetracker.navigation.CategoryFormRoute
import com.software.financetracker.navigation.ExpenseFormRoute
import com.software.financetracker.navigation.GoalDetailRoute
import com.software.financetracker.navigation.GoalFormRoute
import com.software.financetracker.navigation.GoalListRoute
import com.software.financetracker.navigation.IncomeFormRoute
import com.software.financetracker.navigation.IncomeListRoute
import com.software.financetracker.navigation.MetricsRoute
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreenRoot(navController: NavController) {
    val viewModel: HomeViewModel = koinViewModel()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is HomeEvent.NavigateToCategoryDetail ->
                navController.navigate(CategoryDetailRoute(event.categoryId, event.selectedMonth))

            HomeEvent.NavigateToAddCategory ->
                navController.navigate(CategoryFormRoute())

            HomeEvent.NavigateToMetrics ->
                navController.navigate(MetricsRoute)

            HomeEvent.NavigateToAddExpense ->
                navController.navigate(ExpenseFormRoute())

            HomeEvent.NavigateToAddIncome ->
                navController.navigate(IncomeFormRoute())

            HomeEvent.NavigateToAddGoal ->
                navController.navigate(GoalFormRoute())

            HomeEvent.NavigateToIncomeList ->
                navController.navigate(IncomeListRoute)

            is HomeEvent.NavigateToGoalDetail ->
                navController.navigate(GoalDetailRoute(event.goalId))

            HomeEvent.NavigateToGoalList ->
                navController.navigate(GoalListRoute)
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(state = state, onAction = viewModel::onAction)
}
