package com.software.financetracker.feature.investment.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun InvestmentEntryFormScreenRoot(navController: NavController) {
    val viewModel: InvestmentEntryFormViewModel = koinViewModel()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            InvestmentEntryFormEvent.NavigateBack -> navController.navigateUp()
            is InvestmentEntryFormEvent.ShowError -> Unit
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    InvestmentEntryFormScreen(state = state, onAction = viewModel::onAction)
}
