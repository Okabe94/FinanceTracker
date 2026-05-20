package com.software.financetracker.feature.investment.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import com.software.financetracker.navigation.InvestmentDetailRoute
import com.software.financetracker.navigation.InvestmentFormRoute
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun InvestmentListScreenRoot(navController: NavController) {
    val viewModel: InvestmentListViewModel = koinViewModel()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            InvestmentListEvent.NavigateToAddForm ->
                navController.navigate(InvestmentFormRoute())
            is InvestmentListEvent.NavigateToDetail ->
                navController.navigate(InvestmentDetailRoute(investmentId = event.investmentId))
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    InvestmentListScreen(state = state, onAction = viewModel::onAction)
}
