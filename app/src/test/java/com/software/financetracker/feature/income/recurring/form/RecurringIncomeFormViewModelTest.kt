package com.software.financetracker.feature.income.recurring.form

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import com.software.financetracker.data.local.income.RecurringIncomeEntity
import com.software.financetracker.fake.FakeRecurringIncomeRepository
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
class RecurringIncomeFormViewModelTest {

    private lateinit var repository: FakeRecurringIncomeRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleTemplate = RecurringIncomeEntity(
        id = 7L,
        amountCop = 3_000_000L,
        source = "Salario",
        notes = "",
        recurrenceType = "MONTHLY",
        startDate = "2024-01-01",
        nextDueDate = "2024-02-01",
        isActive = true
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeRecurringIncomeRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(recurringIncomeId: Long? = null): RecurringIncomeFormViewModel {
        val savedStateHandle = SavedStateHandle(
            mapOf("recurringIncomeId" to recurringIncomeId)
        )
        return RecurringIncomeFormViewModel(savedStateHandle, repository)
    }

    @Test
    fun `save with invalid amount sets amountError`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(RecurringIncomeFormAction.OnAmountChange("abc"))
        viewModel.onAction(RecurringIncomeFormAction.OnSaveClick)
        assertThat(viewModel.state.value.amountError).isNotNull()
    }

    @Test
    fun `save with zero amount sets amountError`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(RecurringIncomeFormAction.OnAmountChange("0"))
        viewModel.onAction(RecurringIncomeFormAction.OnSaveClick)
        assertThat(viewModel.state.value.amountError).isNotNull()
    }

    @Test
    fun `save valid template emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(RecurringIncomeFormAction.OnAmountChange("3000000"))
        viewModel.events.test {
            viewModel.onAction(RecurringIncomeFormAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(RecurringIncomeFormEvent.NavigateBack)
        }
    }

    @Test
    fun `save when repo returns error emits ShowError`() = runTest {
        repository.shouldReturnError = true
        val viewModel = buildViewModel()
        viewModel.onAction(RecurringIncomeFormAction.OnAmountChange("3000000"))
        viewModel.events.test {
            viewModel.onAction(RecurringIncomeFormAction.OnSaveClick)
            assertThat(awaitItem()).isInstanceOf(RecurringIncomeFormEvent.ShowError::class)
        }
    }

    @Test
    fun `delete existing template emits NavigateBack`() = runTest {
        repository.seed(sampleTemplate)
        val viewModel = buildViewModel(recurringIncomeId = 7L)
        viewModel.events.test {
            viewModel.onAction(RecurringIncomeFormAction.OnDeleteConfirm)
            assertThat(awaitItem()).isEqualTo(RecurringIncomeFormEvent.NavigateBack)
        }
    }

    @Test
    fun `back click emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(RecurringIncomeFormAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(RecurringIncomeFormEvent.NavigateBack)
        }
    }
}
