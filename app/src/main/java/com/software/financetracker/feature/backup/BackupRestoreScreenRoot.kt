package com.software.financetracker.feature.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BackupRestoreScreenRoot(navController: NavController) {
    val viewModel: BackupViewModel = koinViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) viewModel.onAction(BackupAction.OnImportFileSelected(uri))
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            BackupEvent.NavigateBack -> navController.navigateUp()
            BackupEvent.LaunchFilePicker -> filePicker.launch(arrayOf("application/json", "*/*"))
            is BackupEvent.ShowSnackbar ->
                scope.launch { snackbarHostState.showSnackbar(event.message) }
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    BackupRestoreScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction
    )
}
