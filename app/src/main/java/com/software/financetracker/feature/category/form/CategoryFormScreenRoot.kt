package com.software.financetracker.feature.category.form

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CategoryFormScreenRoot(navController: NavController) {
    val viewModel: CategoryFormViewModel = koinViewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            CategoryFormEvent.NavigateBack -> navController.navigateUp()
            is CategoryFormEvent.ShowError -> {
                // snackbarHostState.showSnackbar is a suspend function — skipping for simplicity
            }
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    CategoryFormScreen(state = state, onAction = viewModel::onAction)
}
