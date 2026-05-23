package com.software.financetracker.feature.goal.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GoalFormScreenRoot(navController: NavController) {
    val viewModel: GoalFormViewModel = koinViewModel()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            GoalFormEvent.NavigateBack -> navController.navigateUp()
            is GoalFormEvent.ShowError -> { }
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    GoalFormScreen(state = state, onAction = viewModel::onAction)
}
