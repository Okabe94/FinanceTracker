package com.software.financetracker.feature.investment.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun InvestmentFormScreenRoot(navController: NavController) {
    val viewModel: InvestmentFormViewModel = koinViewModel()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            InvestmentFormEvent.NavigateBack -> navController.navigateUp()
            is InvestmentFormEvent.ShowError -> Unit
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    InvestmentFormScreen(state = state, onAction = viewModel::onAction)
}
