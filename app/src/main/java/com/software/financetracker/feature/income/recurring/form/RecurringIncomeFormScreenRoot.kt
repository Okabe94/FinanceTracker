package com.software.financetracker.feature.income.recurring.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RecurringIncomeFormScreenRoot(navController: NavController) {
    val viewModel: RecurringIncomeFormViewModel = koinViewModel()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RecurringIncomeFormEvent.NavigateBack -> navController.navigateUp()
            is RecurringIncomeFormEvent.ShowError -> { }
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    RecurringIncomeFormScreen(state = state, onAction = viewModel::onAction)
}
