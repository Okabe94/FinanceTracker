package com.software.financetracker.feature.goal.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import com.software.financetracker.navigation.GoalDetailRoute
import com.software.financetracker.navigation.GoalFormRoute
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GoalListScreenRoot(navController: NavController) {
    val viewModel: GoalListViewModel = koinViewModel()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            GoalListEvent.NavigateBack -> navController.navigateUp()
            GoalListEvent.NavigateToAddForm -> navController.navigate(GoalFormRoute())
            is GoalListEvent.NavigateToDetail -> navController.navigate(GoalDetailRoute(event.goalId))
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    GoalListScreen(state = state, onAction = viewModel::onAction)
}
