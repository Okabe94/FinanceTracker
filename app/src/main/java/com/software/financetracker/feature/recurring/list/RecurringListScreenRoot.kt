package com.software.financetracker.feature.recurring.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import com.software.financetracker.navigation.RecurringExpenseFormRoute
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RecurringListScreenRoot(navController: NavController) {
    val viewModel: RecurringListViewModel = koinViewModel()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RecurringListEvent.NavigateBack -> navController.navigateUp()
            RecurringListEvent.NavigateToAddForm ->
                navController.navigate(RecurringExpenseFormRoute())
            is RecurringListEvent.NavigateToEditForm ->
                navController.navigate(RecurringExpenseFormRoute(recurringExpenseId = event.templateId))
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    RecurringListScreen(state = state, onAction = viewModel::onAction)
}
