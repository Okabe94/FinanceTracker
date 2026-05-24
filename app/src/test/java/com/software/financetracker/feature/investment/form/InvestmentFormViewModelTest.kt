package com.software.financetracker.feature.investment.form

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.software.financetracker.data.local.investment.InvestmentEntity
import com.software.financetracker.fake.FakeInvestmentRepository
import com.software.financetracker.fake.FakeUserPreferencesRepository
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
class InvestmentFormViewModelTest {

    private lateinit var investmentRepository: FakeInvestmentRepository
    private val fakePrefs = FakeUserPreferencesRepository()
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleInvestment = InvestmentEntity(
        id = 1L,
        name = "CDT Bancolombia",
        currency = "COP",
        colorArgb = 0xFF039BE5.toInt(),
        iconKey = "savings",
        annualRatePercent = 10.0,
        maturityDate = "2025-12-31",
        createdDate = "2024-01-01"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        investmentRepository = FakeInvestmentRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(investmentId: Long? = null): InvestmentFormViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("investmentId" to investmentId))
        return InvestmentFormViewModel(savedStateHandle, investmentRepository, fakePrefs)
    }

    @Test
    fun `save with blank name sets nameError`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(InvestmentFormAction.OnNameChange(""))
        viewModel.onAction(InvestmentFormAction.OnSaveClick)
        assertThat(viewModel.state.value.nameError).isNotNull()
    }

    @Test
    fun `save with fixed ROI enabled and invalid rate sets annualRateError`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(InvestmentFormAction.OnNameChange("Mi inversión"))
        viewModel.onAction(InvestmentFormAction.OnFixedRoiToggle(true))
        viewModel.onAction(InvestmentFormAction.OnAnnualRateChange("invalid"))
        viewModel.onAction(InvestmentFormAction.OnSaveClick)
        assertThat(viewModel.state.value.annualRateError).isNotNull()
    }

    @Test
    fun `save valid investment emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(InvestmentFormAction.OnNameChange("Mi inversión"))
        viewModel.events.test {
            viewModel.onAction(InvestmentFormAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(InvestmentFormEvent.NavigateBack)
        }
    }

    @Test
    fun `save when repo returns error emits ShowError`() = runTest {
        investmentRepository.shouldReturnError = true
        val viewModel = buildViewModel()
        viewModel.onAction(InvestmentFormAction.OnNameChange("Mi inversión"))
        viewModel.events.test {
            viewModel.onAction(InvestmentFormAction.OnSaveClick)
            assertThat(awaitItem()).isInstanceOf(InvestmentFormEvent.ShowError::class)
        }
    }

    @Test
    fun `delete existing investment emits NavigateBack`() = runTest {
        investmentRepository.seed(sampleInvestment)
        val viewModel = buildViewModel(investmentId = 1L)
        viewModel.events.test {
            viewModel.onAction(InvestmentFormAction.OnDeleteConfirm)
            assertThat(awaitItem()).isEqualTo(InvestmentFormEvent.NavigateBack)
        }
    }

    @Test
    fun `back click emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(InvestmentFormAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(InvestmentFormEvent.NavigateBack)
        }
    }

    @Test
    fun `loading existing investment populates state fields`() = runTest {
        investmentRepository.seed(sampleInvestment)
        val viewModel = buildViewModel(investmentId = 1L)
        val state = viewModel.state.value
        assertThat(state.name).isEqualTo("CDT Bancolombia")
        assertThat(state.selectedCurrency).isEqualTo("COP")
        assertThat(state.hasFixedRoi).isTrue()
        assertThat(state.annualRateInput).isEqualTo("10.0")
        assertThat(state.investmentId).isEqualTo(1L)
    }
}
