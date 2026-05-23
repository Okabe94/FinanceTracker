package com.software.financetracker.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import com.software.financetracker.navigation.CategoryDetailRoute
import com.software.financetracker.navigation.CategoryFormRoute
import com.software.financetracker.navigation.GoalListRoute
import com.software.financetracker.navigation.IncomeFormRoute
import com.software.financetracker.navigation.IncomeListRoute
import com.software.financetracker.navigation.MetricsRoute
import com.software.financetracker.navigation.RecurringListRoute
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

            HomeEvent.NavigateToRecurringExpenses ->
                navController.navigate(RecurringListRoute)

            HomeEvent.NavigateToAddIncome ->
                navController.navigate(IncomeFormRoute())

            HomeEvent.NavigateToIncomeList ->
                navController.navigate(IncomeListRoute)

            HomeEvent.NavigateToGoals ->
                navController.navigate(GoalListRoute)
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(state = state, onAction = viewModel::onAction)
}
