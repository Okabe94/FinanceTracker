package com.software.financetracker.feature.investment.entry

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import com.software.financetracker.data.local.investment.InvestmentEntryEntity
import com.software.financetracker.data.local.investment.InvestmentEntity
import com.software.financetracker.domain.model.investment.EntryType
import com.software.financetracker.fake.FakeInvestmentEntryRepository
import com.software.financetracker.fake.FakeInvestmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31], application = android.app.Application::class)
class InvestmentEntryFormViewModelTest {

    private lateinit var investmentRepository: FakeInvestmentRepository
    private lateinit var entryRepository: FakeInvestmentEntryRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleInvestment = InvestmentEntity(
        id = 1L, name = "CDT", currency = "COP",
        colorArgb = 0xFF039BE5.toInt(), iconKey = "savings", createdDate = "2024-01-01"
    )

    private val sampleEntry = InvestmentEntryEntity(
        id = 10L, investmentId = 1L,
        entryType = EntryType.CASH_INJECTION.storageKey,
        amountMinorUnits = 500_000L, date = "2024-06-01"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        investmentRepository = FakeInvestmentRepository()
        entryRepository = FakeInvestmentEntryRepository()
        investmentRepository.seed(sampleInvestment)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(investmentId: Long = 1L, entryId: Long? = null): InvestmentEntryFormViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("investmentId" to investmentId, "entryId" to entryId))
        return InvestmentEntryFormViewModel(savedStateHandle, investmentRepository, entryRepository)
    }

    @Test
    fun `selecting NOTE type sets showAmountField to false`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(InvestmentEntryFormAction.OnTypeSelected(EntryType.NOTE))
        assertThat(viewModel.state.value.showAmountField).isFalse()
    }

    @Test
    fun `save NOTE type succeeds without any amount`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(InvestmentEntryFormAction.OnTypeSelected(EntryType.NOTE))
        viewModel.events.test {
            viewModel.onAction(InvestmentEntryFormAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(InvestmentEntryFormEvent.NavigateBack)
        }
    }

    @Test
    fun `save CASH_INJECTION with invalid amount sets amountError`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(InvestmentEntryFormAction.OnTypeSelected(EntryType.CASH_INJECTION))
        viewModel.onAction(InvestmentEntryFormAction.OnAmountChange("abc"))
        viewModel.onAction(InvestmentEntryFormAction.OnSaveClick)
        assertThat(viewModel.state.value.amountError).isNotNull()
    }

    @Test
    fun `save valid entry emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(InvestmentEntryFormAction.OnTypeSelected(EntryType.CASH_INJECTION))
        viewModel.onAction(InvestmentEntryFormAction.OnAmountChange("100000"))
        viewModel.events.test {
            viewModel.onAction(InvestmentEntryFormAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(InvestmentEntryFormEvent.NavigateBack)
        }
    }

    @Test
    fun `save when repo returns error emits ShowError`() = runTest {
        entryRepository.shouldReturnError = true
        val viewModel = buildViewModel()
        viewModel.onAction(InvestmentEntryFormAction.OnTypeSelected(EntryType.CASH_INJECTION))
        viewModel.onAction(InvestmentEntryFormAction.OnAmountChange("100000"))
        viewModel.events.test {
            viewModel.onAction(InvestmentEntryFormAction.OnSaveClick)
            assertThat(awaitItem()).isInstanceOf(InvestmentEntryFormEvent.ShowError::class)
        }
    }

    @Test
    fun `delete entry emits NavigateBack`() = runTest {
        entryRepository.seed(sampleEntry)
        val viewModel = buildViewModel(entryId = 10L)
        viewModel.events.test {
            viewModel.onAction(InvestmentEntryFormAction.OnDeleteConfirm)
            assertThat(awaitItem()).isEqualTo(InvestmentEntryFormEvent.NavigateBack)
        }
    }

    @Test
    fun `back click emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(InvestmentEntryFormAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(InvestmentEntryFormEvent.NavigateBack)
        }
    }
}
