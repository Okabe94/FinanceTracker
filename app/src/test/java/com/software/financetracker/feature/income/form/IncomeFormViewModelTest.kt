package com.software.financetracker.feature.income.form

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import com.software.financetracker.data.local.income.IncomeEntity
import com.software.financetracker.fake.FakeIncomeRepository
import com.software.financetracker.feature.income.IncomeSourceType
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
class IncomeFormViewModelTest {

    private lateinit var incomeRepository: FakeIncomeRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleIncome = IncomeEntity(
        id = 1L,
        amountCop = 3_000_000L,
        source = "Salario",
        date = "2024-01-15",
        notes = "Enero"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        incomeRepository = FakeIncomeRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(incomeId: Long? = null): IncomeFormViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("incomeId" to incomeId))
        return IncomeFormViewModel(savedStateHandle, incomeRepository)
    }

    @Test
    fun `save with invalid amount sets amountError`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(IncomeFormAction.OnAmountChange(""))
        viewModel.onAction(IncomeFormAction.OnSaveClick)
        assertThat(viewModel.state.value.amountError).isNotNull()
    }

    @Test
    fun `save with zero amount sets amountError`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(IncomeFormAction.OnAmountChange("0"))
        viewModel.onAction(IncomeFormAction.OnSaveClick)
        assertThat(viewModel.state.value.amountError).isNotNull()
    }

    @Test
    fun `save with OTHER type and blank custom source sets amountError`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(IncomeFormAction.OnAmountChange("500000"))
        viewModel.onAction(IncomeFormAction.OnSourceTypeSelected(IncomeSourceType.OTHER))
        viewModel.onAction(IncomeFormAction.OnCustomSourceChange(""))
        viewModel.onAction(IncomeFormAction.OnSaveClick)
        assertThat(viewModel.state.value.amountError).isNotNull()
    }

    @Test
    fun `save valid income with default source emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(IncomeFormAction.OnAmountChange("3000000"))
        viewModel.events.test {
            viewModel.onAction(IncomeFormAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(IncomeFormEvent.NavigateBack)
        }
    }

    @Test
    fun `save when repo returns error emits ShowError`() = runTest {
        incomeRepository.shouldReturnError = true
        val viewModel = buildViewModel()
        viewModel.onAction(IncomeFormAction.OnAmountChange("3000000"))
        viewModel.events.test {
            viewModel.onAction(IncomeFormAction.OnSaveClick)
            assertThat(awaitItem()).isInstanceOf(IncomeFormEvent.ShowError::class)
        }
    }

    @Test
    fun `delete existing income emits NavigateBack`() = runTest {
        incomeRepository.seed(sampleIncome)
        val viewModel = buildViewModel(incomeId = 1L)
        viewModel.events.test {
            viewModel.onAction(IncomeFormAction.OnDeleteConfirm)
            assertThat(awaitItem()).isEqualTo(IncomeFormEvent.NavigateBack)
        }
    }

    @Test
    fun `back click emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(IncomeFormAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(IncomeFormEvent.NavigateBack)
        }
    }

    @Test
    fun `loading existing income populates state fields`() = runTest {
        incomeRepository.seed(sampleIncome)
        val viewModel = buildViewModel(incomeId = 1L)
        val state = viewModel.state.value
        assertThat(state.amountInput).isEqualTo("3000000")
        assertThat(state.selectedSourceType).isEqualTo(IncomeSourceType.SALARY)
        assertThat(state.notes).isEqualTo("Enero")
        assertThat(state.incomeId).isEqualTo(1L)
    }
}
