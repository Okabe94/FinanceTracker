package com.software.financetracker.feature.investment.detail

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.export.ExportShareHelper
import com.software.financetracker.core.presentation.ObserveAsEvents
import com.software.financetracker.navigation.InvestmentEntryFormRoute
import com.software.financetracker.navigation.InvestmentFormRoute
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun InvestmentDetailScreenRoot(navController: NavController) {
    val viewModel: InvestmentDetailViewModel = koinViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
            InvestmentDetailEvent.ShowUndoSnackbar -> {
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Movimiento eliminado",
                        actionLabel = "Deshacer"
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onAction(InvestmentDetailAction.UndoDeleteEntry)
                    }
                }
            }
            is InvestmentDetailEvent.SaveInvestmentCsv ->
                ExportShareHelper.save(context, event.csvContent)
            is InvestmentDetailEvent.ShareInvestmentCsv ->
                ExportShareHelper.share(context, event.csvContent)
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    InvestmentDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}
