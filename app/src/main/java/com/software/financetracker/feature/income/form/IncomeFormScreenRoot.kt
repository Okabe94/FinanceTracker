package com.software.financetracker.feature.income.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun IncomeFormScreenRoot(navController: NavController) {
    val viewModel: IncomeFormViewModel = koinViewModel()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            IncomeFormEvent.NavigateBack -> navController.navigateUp()
            is IncomeFormEvent.ShowError -> { /* could show snackbar */ }
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    IncomeFormScreen(state = state, onAction = viewModel::onAction)
}
