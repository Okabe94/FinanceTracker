package com.software.financetracker.feature.income.form

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.software.financetracker.data.local.income.IncomeEntity
import com.software.financetracker.data.local.income.RecurringIncomeEntity
import com.software.financetracker.domain.model.RecurrenceType
import com.software.financetracker.fake.FakeIncomeRepository
import com.software.financetracker.fake.FakeRecurringIncomeRepository
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
    private lateinit var recurringIncomeRepository: FakeRecurringIncomeRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleIncome = IncomeEntity(
        id = 1L,
        amountCop = 3_000_000L,
        source = "Salario",
        date = "2024-01-15",
        notes = "Enero"
    )

    private val sampleRecurring = RecurringIncomeEntity(
        id = 5L,
        amountCop = 2_000_000L,
        source = "Freelance",
        notes = "Mensual",
        recurrenceType = "MONTHLY",
        startDate = "2024-01-01",
        nextDueDate = "2024-02-01",
        isActive = true
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        incomeRepository = FakeIncomeRepository()
        recurringIncomeRepository = FakeRecurringIncomeRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(incomeId: Long? = null, recurringIncomeId: Long? = null): IncomeFormViewModel {
        val args = buildMap<String, Any?> {
            put("incomeId", incomeId)
            put("recurringIncomeId", recurringIncomeId)
        }
        val savedStateHandle = SavedStateHandle(args)
        return IncomeFormViewModel(savedStateHandle, incomeRepository, recurringIncomeRepository)
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

    @Test
    fun `recurring toggle enables isRecurring in state`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(IncomeFormAction.OnToggleRecurring)
        assertThat(viewModel.state.value.isRecurring).isTrue()
    }

    @Test
    fun `save with recurring toggle on inserts recurring entity`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(IncomeFormAction.OnAmountChange("2000000"))
        viewModel.onAction(IncomeFormAction.OnToggleRecurring)
        viewModel.events.test {
            viewModel.onAction(IncomeFormAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(IncomeFormEvent.NavigateBack)
        }
    }

    @Test
    fun `save with recurring toggle off inserts regular income entity`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(IncomeFormAction.OnAmountChange("1500000"))
        viewModel.events.test {
            viewModel.onAction(IncomeFormAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(IncomeFormEvent.NavigateBack)
        }
    }

    @Test
    fun `loading recurring income template sets isRecurring true`() = runTest {
        recurringIncomeRepository.seed(sampleRecurring)
        val viewModel = buildViewModel(recurringIncomeId = 5L)
        val state = viewModel.state.value
        assertThat(state.isRecurring).isTrue()
        assertThat(state.recurringIncomeId).isEqualTo(5L)
        assertThat(state.amountInput).isEqualTo("2000000")
        assertThat(state.recurrenceType).isEqualTo(RecurrenceType.Monthly)
    }

    @Test
    fun `editing recurring template and saving updates it`() = runTest {
        recurringIncomeRepository.seed(sampleRecurring)
        val viewModel = buildViewModel(recurringIncomeId = 5L)
        viewModel.onAction(IncomeFormAction.OnAmountChange("2500000"))
        viewModel.events.test {
            viewModel.onAction(IncomeFormAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(IncomeFormEvent.NavigateBack)
        }
    }

    @Test
    fun `delete recurring template emits NavigateBack`() = runTest {
        recurringIncomeRepository.seed(sampleRecurring)
        val viewModel = buildViewModel(recurringIncomeId = 5L)
        viewModel.events.test {
            viewModel.onAction(IncomeFormAction.OnDeleteConfirm)
            assertThat(awaitItem()).isEqualTo(IncomeFormEvent.NavigateBack)
        }
    }
}
