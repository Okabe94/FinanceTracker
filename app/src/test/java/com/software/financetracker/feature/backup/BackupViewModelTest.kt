package com.software.financetracker.feature.backup

import android.net.Uri
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.software.financetracker.fake.FakeBackupFileOps
import com.software.financetracker.fake.FakeBackupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31], application = android.app.Application::class)
class BackupViewModelTest {

    private lateinit var repository: FakeBackupRepository
    private lateinit var fileOps: FakeBackupFileOps
    private val testDispatcher = UnconfinedTestDispatcher()
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeBackupRepository()
        fileOps = FakeBackupFileOps()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = BackupViewModel(repository, fileOps)

    // region Export Save

    @Test
    fun `onExportSave_success_showsSuccessSnackbar`() = runTest {
        val vm = buildViewModel()
        vm.events.test {
            vm.onAction(BackupAction.OnExportSave)
            val event = awaitItem()
            assertThat(event).isInstanceOf(BackupEvent.ShowSnackbar::class)
            assertThat((event as BackupEvent.ShowSnackbar).message).contains("Descargas")
        }
    }

    @Test
    fun `onExportSave_fileSaveFails_showsErrorSnackbar`() = runTest {
        fileOps.saveResult = false
        val vm = buildViewModel()
        vm.events.test {
            vm.onAction(BackupAction.OnExportSave)
            val event = awaitItem() as BackupEvent.ShowSnackbar
            assertThat(event.message).contains("Error")
        }
    }

    @Test
    fun `onExportSave_repositoryThrows_showsErrorSnackbar`() = runTest {
        repository.shouldThrowOnExport = true
        val vm = buildViewModel()
        vm.events.test {
            vm.onAction(BackupAction.OnExportSave)
            val event = awaitItem() as BackupEvent.ShowSnackbar
            assertThat(event.message).contains("Error")
        }
    }

    @Test
    fun `onExportSave_resetsLoadingAfterCompletion`() = runTest {
        val vm = buildViewModel()
        vm.events.test {
            vm.onAction(BackupAction.OnExportSave)
            awaitItem() // consume the ShowSnackbar event so the finally block runs
            cancelAndIgnoreRemainingEvents()
        }
        assertThat(vm.state.value.isLoading).isFalse()
    }

    // endregion

    // region Export Share

    @Test
    fun `onExportShare_success_sharesFile`() = runTest {
        val vm = buildViewModel()
        vm.onAction(BackupAction.OnExportShare)
        assertThat(fileOps.sharedFiles.isNotEmpty()).isTrue()
    }

    @Test
    fun `onExportShare_repositoryThrows_showsErrorSnackbar`() = runTest {
        repository.shouldThrowOnExport = true
        val vm = buildViewModel()
        vm.events.test {
            vm.onAction(BackupAction.OnExportShare)
            val event = awaitItem() as BackupEvent.ShowSnackbar
            assertThat(event.message).contains("Error")
        }
    }

    // endregion

    // region Pick File

    @Test
    fun `onPickFileClick_emitsLaunchFilePicker`() = runTest {
        val vm = buildViewModel()
        vm.events.test {
            vm.onAction(BackupAction.OnPickFileClick)
            assertThat(awaitItem()).isInstanceOf(BackupEvent.LaunchFilePicker::class)
        }
    }

    // endregion

    // region Import Flow

    @Test
    fun `onImportFileSelected_validJson_showsConfirmDialog`() = runTest {
        val backupJson = json.encodeToString(repository.sampleBackupData)
        fileOps.readResult = backupJson
        val vm = buildViewModel()

        vm.state.test {
            awaitItem() // initial state
            vm.onAction(BackupAction.OnImportFileSelected(Uri.EMPTY))
            val state = awaitItem()
            assertThat(state.showImportConfirmDialog).isTrue()
            assertThat(state.pendingImportData).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onImportFileSelected_invalidJson_showsErrorSnackbar`() = runTest {
        fileOps.readResult = "{ invalid json }"
        val vm = buildViewModel()
        vm.events.test {
            vm.onAction(BackupAction.OnImportFileSelected(Uri.EMPTY))
            val event = awaitItem() as BackupEvent.ShowSnackbar
            assertThat(event.message).contains("inválido")
        }
    }

    @Test
    fun `onImportFileSelected_readFails_showsErrorSnackbar`() = runTest {
        fileOps.shouldThrowOnRead = true
        val vm = buildViewModel()
        vm.events.test {
            vm.onAction(BackupAction.OnImportFileSelected(Uri.EMPTY))
            val event = awaitItem() as BackupEvent.ShowSnackbar
            assertThat(event.message).contains("inválido")
        }
    }

    @Test
    fun `onImportDismiss_clearsDialog`() = runTest {
        val backupJson = json.encodeToString(repository.sampleBackupData)
        fileOps.readResult = backupJson
        val vm = buildViewModel()

        vm.onAction(BackupAction.OnImportFileSelected(Uri.EMPTY))
        vm.onAction(BackupAction.OnImportDismiss)

        assertThat(vm.state.value.showImportConfirmDialog).isFalse()
        assertThat(vm.state.value.pendingImportData).isNull()
    }

    @Test
    fun `onImportConfirm_callsRepositoryImport_andShowsSuccess`() = runTest {
        val backupJson = json.encodeToString(repository.sampleBackupData)
        fileOps.readResult = backupJson
        val vm = buildViewModel()
        vm.onAction(BackupAction.OnImportFileSelected(Uri.EMPTY))

        vm.events.test {
            vm.onAction(BackupAction.OnImportConfirm)
            val event = awaitItem() as BackupEvent.ShowSnackbar
            assertThat(event.message).contains("restaurados")
            assertThat(repository.importedData).isNotNull()
        }
    }

    @Test
    fun `onImportConfirm_repositoryThrows_showsErrorSnackbar`() = runTest {
        val backupJson = json.encodeToString(repository.sampleBackupData)
        fileOps.readResult = backupJson
        repository.shouldThrowOnImport = true
        val vm = buildViewModel()
        vm.onAction(BackupAction.OnImportFileSelected(Uri.EMPTY))

        vm.events.test {
            vm.onAction(BackupAction.OnImportConfirm)
            val event = awaitItem() as BackupEvent.ShowSnackbar
            assertThat(event.message).contains("Error")
        }
    }

    @Test
    fun `onImportConfirm_resetsLoadingAfterCompletion`() = runTest {
        val backupJson = json.encodeToString(repository.sampleBackupData)
        fileOps.readResult = backupJson
        val vm = buildViewModel()
        vm.onAction(BackupAction.OnImportFileSelected(Uri.EMPTY))
        vm.events.test {
            vm.onAction(BackupAction.OnImportConfirm)
            awaitItem() // consume the ShowSnackbar event so the finally block runs
            cancelAndIgnoreRemainingEvents()
        }
        assertThat(vm.state.value.isLoading).isFalse()
    }

    // endregion

    // region Navigation

    @Test
    fun `onBackClick_emitsNavigateBack`() = runTest {
        val vm = buildViewModel()
        vm.events.test {
            vm.onAction(BackupAction.OnBackClick)
            assertThat(awaitItem()).isInstanceOf(BackupEvent.NavigateBack::class)
        }
    }

    // endregion
}
