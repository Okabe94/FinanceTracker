package com.software.financetracker.feature.backup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.software.financetracker.core.backup.BackupData
import com.software.financetracker.core.backup.BackupFileOps
import com.software.financetracker.core.backup.BackupRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BackupViewModel(
    private val backupRepository: BackupRepository,
    private val fileOps: BackupFileOps
) : ViewModel() {

    private val _state = MutableStateFlow(BackupState())
    val state = _state.asStateFlow()

    private val _events = Channel<BackupEvent>()
    val events = _events.receiveAsFlow()

    private val json = Json { ignoreUnknownKeys = true }

    fun onAction(action: BackupAction) {
        when (action) {
            BackupAction.OnBackClick ->
                viewModelScope.launch { _events.send(BackupEvent.NavigateBack) }

            BackupAction.OnExportSave -> exportAndSave()
            BackupAction.OnExportShare -> exportAndShare()

            BackupAction.OnPickFileClick ->
                viewModelScope.launch { _events.send(BackupEvent.LaunchFilePicker) }

            is BackupAction.OnImportFileSelected -> readAndStageImport(action.uri)
            BackupAction.OnImportConfirm -> confirmImport()
            BackupAction.OnImportDismiss ->
                _state.update { it.copy(showImportConfirmDialog = false, pendingImportData = null) }
        }
    }

    private fun exportAndSave() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val backupData = backupRepository.export()
                val jsonString = json.encodeToString<BackupData>(backupData)
                val filename = buildFilename()
                val saved = fileOps.saveToDownloads(jsonString, filename)
                _events.send(
                    BackupEvent.ShowSnackbar(
                        if (saved) "Respaldo guardado en Descargas" else "Error al guardar el respaldo"
                    )
                )
            } catch (e: Exception) {
                _events.send(BackupEvent.ShowSnackbar("Error al exportar los datos"))
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun exportAndShare() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val backupData = backupRepository.export()
                val jsonString = json.encodeToString<BackupData>(backupData)
                val filename = buildFilename()
                fileOps.shareJson(jsonString, filename)
            } catch (e: Exception) {
                _events.send(BackupEvent.ShowSnackbar("Error al compartir el respaldo"))
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun readAndStageImport(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val jsonString = fileOps.readFromUri(uri)
                val backupData = json.decodeFromString<BackupData>(jsonString)
                _state.update {
                    it.copy(
                        isLoading = false,
                        showImportConfirmDialog = true,
                        pendingImportData = backupData
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _events.send(BackupEvent.ShowSnackbar("Archivo de respaldo inválido"))
            }
        }
    }

    private fun confirmImport() {
        val data = _state.value.pendingImportData ?: return
        _state.update { it.copy(showImportConfirmDialog = false, pendingImportData = null, isLoading = true) }
        viewModelScope.launch {
            try {
                backupRepository.import(data)
                _events.send(BackupEvent.ShowSnackbar("Datos restaurados correctamente"))
            } catch (e: Exception) {
                _events.send(BackupEvent.ShowSnackbar("Error al restaurar los datos"))
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun buildFilename(): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"))
        return "financetracker_backup_$timestamp.json"
    }
}
