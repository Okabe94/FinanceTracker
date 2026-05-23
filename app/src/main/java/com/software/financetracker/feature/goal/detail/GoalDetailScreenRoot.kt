package com.software.financetracker.feature.goal.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import com.software.financetracker.navigation.GoalFormRoute
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GoalDetailScreenRoot(navController: NavController) {
    val viewModel: GoalDetailViewModel = koinViewModel()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            GoalDetailEvent.NavigateBack -> navController.navigateUp()
            is GoalDetailEvent.NavigateToEdit -> navController.navigate(GoalFormRoute(goalId = event.goalId))
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    GoalDetailScreen(state = state, onAction = viewModel::onAction)
}
