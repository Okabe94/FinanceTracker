package com.software.financetracker.feature.expense.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExpenseFormScreenRoot(navController: NavController) {
    val viewModel: ExpenseFormViewModel = koinViewModel()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ExpenseFormEvent.NavigateBack -> navController.navigateUp()
            is ExpenseFormEvent.ShowError -> { }
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    ExpenseFormScreen(state = state, onAction = viewModel::onAction)
}
