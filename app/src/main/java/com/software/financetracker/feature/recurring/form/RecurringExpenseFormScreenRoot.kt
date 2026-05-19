package com.software.financetracker.feature.recurring.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RecurringExpenseFormScreenRoot(navController: NavController) {
    val viewModel: RecurringExpenseFormViewModel = koinViewModel()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RecurringExpenseFormEvent.NavigateBack -> navController.navigateUp()
            is RecurringExpenseFormEvent.ShowError -> { }
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    RecurringExpenseFormScreen(state = state, onAction = viewModel::onAction)
}
