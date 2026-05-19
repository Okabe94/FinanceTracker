package com.software.financetracker.feature.metrics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.export.ExportShareHelper
import com.software.financetracker.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MetricsScreenRoot(navController: NavController) {
    val viewModel: MetricsViewModel = koinViewModel()
    val context = LocalContext.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            MetricsEvent.NavigateBack -> navController.navigateUp()
            is MetricsEvent.SaveReady -> ExportShareHelper.save(context, event.csvContent)
            is MetricsEvent.ShareReady -> ExportShareHelper.share(context, event.csvContent)
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    MetricsScreen(state = state, onAction = viewModel::onAction)
}
