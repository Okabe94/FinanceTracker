package com.software.financetracker.feature.income.recurring.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import com.software.financetracker.navigation.RecurringIncomeFormRoute
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RecurringIncomeListScreenRoot(navController: NavController) {
    val viewModel: RecurringIncomeListViewModel = koinViewModel()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RecurringIncomeListEvent.NavigateBack -> navController.navigateUp()
            RecurringIncomeListEvent.NavigateToAddForm -> navController.navigate(RecurringIncomeFormRoute())
            is RecurringIncomeListEvent.NavigateToEditForm ->
                navController.navigate(RecurringIncomeFormRoute(recurringIncomeId = event.templateId))
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    RecurringIncomeListScreen(state = state, onAction = viewModel::onAction)
}
