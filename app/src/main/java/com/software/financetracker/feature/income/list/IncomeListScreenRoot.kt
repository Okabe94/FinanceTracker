package com.software.financetracker.feature.income.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import com.software.financetracker.navigation.IncomeFormRoute
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun IncomeListScreenRoot(navController: NavController) {
    val viewModel: IncomeListViewModel = koinViewModel()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            IncomeListEvent.NavigateBack -> navController.navigateUp()
            IncomeListEvent.NavigateToAddIncome -> navController.navigate(IncomeFormRoute())
            is IncomeListEvent.NavigateToEditIncome ->
                navController.navigate(IncomeFormRoute(incomeId = event.incomeId))
            is IncomeListEvent.NavigateToEditRecurringIncome ->
                navController.navigate(IncomeFormRoute(recurringIncomeId = event.id))
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    IncomeListScreen(state = state, onAction = viewModel::onAction)
}
