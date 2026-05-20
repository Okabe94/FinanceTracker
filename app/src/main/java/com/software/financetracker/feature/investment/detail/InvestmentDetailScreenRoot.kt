package com.software.financetracker.feature.investment.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import com.software.financetracker.navigation.InvestmentDetailRoute
import com.software.financetracker.navigation.InvestmentEntryFormRoute
import com.software.financetracker.navigation.InvestmentFormRoute
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun InvestmentDetailScreenRoot(navController: NavController) {
    val viewModel: InvestmentDetailViewModel = koinViewModel()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            InvestmentDetailEvent.NavigateBack -> navController.navigateUp()
            is InvestmentDetailEvent.NavigateToEditInvestment ->
                navController.navigate(InvestmentFormRoute(investmentId = event.investmentId))
            is InvestmentDetailEvent.NavigateToAddEntry ->
                navController.navigate(InvestmentEntryFormRoute(investmentId = event.investmentId))
            is InvestmentDetailEvent.NavigateToEditEntry ->
                navController.navigate(
                    InvestmentEntryFormRoute(
                        investmentId = event.investmentId,
                        entryId = event.entryId
                    )
                )
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    InvestmentDetailScreen(state = state, onAction = viewModel::onAction)
}
