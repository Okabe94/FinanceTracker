package com.software.financetracker.feature.expense.batch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BatchExpenseScreenRoot(navController: NavController) {
    val viewModel: BatchExpenseViewModel = koinViewModel()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            BatchExpenseEvent.NavigateBack -> navController.navigateUp()
            is BatchExpenseEvent.ShowError -> { }
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    BatchExpenseScreen(state = state, onAction = viewModel::onAction)
}
