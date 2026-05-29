package com.software.financetracker.feature.investment.entry.batch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BatchInvestmentEntryScreenRoot(navController: NavController) {
    val viewModel: BatchInvestmentEntryViewModel = koinViewModel()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            BatchInvestmentEntryEvent.NavigateBack -> navController.navigateUp()
            is BatchInvestmentEntryEvent.ShowError -> { }
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    BatchInvestmentEntryScreen(state = state, onAction = viewModel::onAction)
}
