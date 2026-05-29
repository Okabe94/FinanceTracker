package com.software.financetracker.feature.backup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
    state: BackupState,
    snackbarHostState: SnackbarHostState,
    onAction: (BackupAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Copia de seguridad") },
                navigationIcon = {
                    IconButton(onClick = { onAction(BackupAction.OnBackClick) }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ExportSection(state, onAction)
            HorizontalDivider()
            ImportSection(state, onAction)
            Spacer(Modifier.height(16.dp))
        }

        if (state.showImportConfirmDialog) {
            ImportConfirmDialog(onAction)
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun ExportSection(state: BackupState, onAction: (BackupAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Exportar")
        Text(
            "Guarda una copia de todos tus datos: categorías, gastos, ingresos, inversiones, metas y configuración.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onAction(BackupAction.OnExportSave) },
                enabled = !state.isLoading,
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_export_save")
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Rounded.Download, contentDescription = null)
                }
                Spacer(Modifier.size(8.dp))
                Text("Descargar")
            }
            OutlinedButton(
                onClick = { onAction(BackupAction.OnExportShare) },
                enabled = !state.isLoading,
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_export_share")
            ) {
                Icon(Icons.Rounded.Share, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Compartir")
            }
        }
    }
}

@Composable
private fun ImportSection(state: BackupState, onAction: (BackupAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Importar")
        Text(
            "Selecciona un archivo de respaldo (.json) para restaurar todos tus datos. Los datos actuales serán reemplazados.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Rounded.Upload,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )
            Text(
                "Esta acción reemplazará todos los datos existentes.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        Button(
            onClick = { onAction(BackupAction.OnPickFileClick) },
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_import_pick")
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.size(8.dp))
                Text("Procesando...")
            } else {
                Icon(Icons.Rounded.Upload, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Seleccionar archivo de respaldo")
            }
        }
    }
}

@Composable
private fun ImportConfirmDialog(onAction: (BackupAction) -> Unit) {
    AlertDialog(
        onDismissRequest = { onAction(BackupAction.OnImportDismiss) },
        title = { Text("¿Restaurar datos?") },
        text = {
            Text("Todos los datos actuales serán eliminados y reemplazados con los datos del archivo de respaldo. Esta acción no se puede deshacer.")
        },
        confirmButton = {
            Button(
                onClick = { onAction(BackupAction.OnImportConfirm) },
                modifier = Modifier.testTag("btn_import_confirm")
            ) {
                Text("Restaurar")
            }
        },
        dismissButton = {
            TextButton(onClick = { onAction(BackupAction.OnImportDismiss) }) {
                Text("Cancelar")
            }
        }
    )
}
