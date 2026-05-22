package com.software.financetracker.feature.expense.assistant

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AssistantExpenseScreenRoot(navController: NavController) {
    val viewModel: AssistantExpenseViewModel = koinViewModel()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            AssistantExpenseEvent.NavigateBack -> navController.navigateUp()
            is AssistantExpenseEvent.ShowError -> { }
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    AssistantExpenseScreen(state = state, onAction = viewModel::onAction)
}
