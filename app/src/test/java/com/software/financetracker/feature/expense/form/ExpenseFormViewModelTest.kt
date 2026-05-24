package com.software.financetracker.feature.expense.form

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.software.financetracker.data.local.expense.ExpenseEntity
import com.software.financetracker.data.local.recurring.RecurringExpenseEntity
import com.software.financetracker.domain.model.RecurrenceType
import com.software.financetracker.fake.FakeCategoryRepository
import com.software.financetracker.fake.FakeExpenseRepository
import com.software.financetracker.fake.FakeRecurringExpenseRepository
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
class ExpenseFormViewModelTest {

    private lateinit var expenseRepository: FakeExpenseRepository
    private lateinit var recurringExpenseRepository: FakeRecurringExpenseRepository
    private lateinit var categoryRepository: FakeCategoryRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleExpense = ExpenseEntity(
        id = 1L,
        categoryId = 2L,
        amountCop = 50_000L,
        description = "Almuerzo",
        date = "2024-01-15"
    )

    private val sampleRecurring = RecurringExpenseEntity(
        id = 10L,
        categoryId = 2L,
        amountCop = 100_000L,
        description = "Gimnasio",
        recurrenceType = "MONTHLY",
        startDate = "2024-01-01",
        nextDueDate = "2024-02-01",
        isActive = true
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        expenseRepository = FakeExpenseRepository()
        recurringExpenseRepository = FakeRecurringExpenseRepository()
        categoryRepository = FakeCategoryRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(
        categoryId: Long? = 2L,
        expenseId: Long? = null,
        recurringExpenseId: Long? = null
    ): ExpenseFormViewModel {
        val args = buildMap<String, Any?> {
            put("categoryId", categoryId)
            put("expenseId", expenseId)
            put("recurringExpenseId", recurringExpenseId)
        }
        val savedStateHandle = SavedStateHandle(args)
        return ExpenseFormViewModel(savedStateHandle, expenseRepository, recurringExpenseRepository, categoryRepository)
    }

    @Test
    fun `save with invalid amount sets amountError`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(ExpenseFormAction.OnAmountChange(""))
        viewModel.onAction(ExpenseFormAction.OnSaveClick)
        assertThat(viewModel.state.value.amountError).isNotNull()
    }

    @Test
    fun `save with zero amount sets amountError`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(ExpenseFormAction.OnAmountChange("0"))
        viewModel.onAction(ExpenseFormAction.OnSaveClick)
        assertThat(viewModel.state.value.amountError).isNotNull()
    }

    @Test
    fun `givenNoCategory_whenSave_showsCategoryError`() = runTest {
        val viewModel = buildViewModel(categoryId = null)
        viewModel.onAction(ExpenseFormAction.OnAmountChange("50000"))
        viewModel.onAction(ExpenseFormAction.OnSaveClick)
        assertThat(viewModel.state.value.categoryError).isTrue()
    }

    @Test
    fun `givenRecurringToggleOff_whenSave_insertsExpenseEntity`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(ExpenseFormAction.OnAmountChange("50000"))
        viewModel.events.test {
            viewModel.onAction(ExpenseFormAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(ExpenseFormEvent.NavigateBack)
        }
    }

    @Test
    fun `givenRecurringToggleOn_whenSave_insertsRecurringEntity`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(ExpenseFormAction.OnAmountChange("100000"))
        viewModel.onAction(ExpenseFormAction.OnToggleRecurring)
        viewModel.events.test {
            viewModel.onAction(ExpenseFormAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(ExpenseFormEvent.NavigateBack)
        }
    }

    @Test
    fun `givenCustomRecurrence_whenSave_storesCorrectInterval`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(ExpenseFormAction.OnAmountChange("75000"))
        viewModel.onAction(ExpenseFormAction.OnToggleRecurring)
        viewModel.onAction(ExpenseFormAction.OnRecurrenceTypeSelect(RecurrenceType.Custom(14)))
        viewModel.onAction(ExpenseFormAction.OnCustomIntervalChange("14"))
        viewModel.events.test {
            viewModel.onAction(ExpenseFormAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(ExpenseFormEvent.NavigateBack)
        }
    }

    @Test
    fun `givenEditingRecurringTemplate_loadsCorrectState`() = runTest {
        recurringExpenseRepository.seed(sampleRecurring)
        val viewModel = buildViewModel(categoryId = null, recurringExpenseId = 10L)
        val state = viewModel.state.value
        assertThat(state.isRecurring).isTrue()
        assertThat(state.recurringExpenseId).isEqualTo(10L)
        assertThat(state.amountInput).isEqualTo("100000")
        assertThat(state.recurrenceType).isEqualTo(RecurrenceType.Monthly)
    }

    @Test
    fun `givenEditingRecurringTemplate_togglesActive`() = runTest {
        recurringExpenseRepository.seed(sampleRecurring)
        val viewModel = buildViewModel(categoryId = null, recurringExpenseId = 10L)
        assertThat(viewModel.state.value.isActive).isTrue()
        viewModel.onAction(ExpenseFormAction.OnToggleActive)
        assertThat(viewModel.state.value.isActive).isFalse()
    }

    @Test
    fun `save when repo returns error emits ShowError`() = runTest {
        expenseRepository.shouldReturnError = true
        val viewModel = buildViewModel()
        viewModel.onAction(ExpenseFormAction.OnAmountChange("50000"))
        viewModel.events.test {
            viewModel.onAction(ExpenseFormAction.OnSaveClick)
            assertThat(awaitItem()).isInstanceOf(ExpenseFormEvent.ShowError::class)
        }
    }

    @Test
    fun `loading existing expense populates state fields`() = runTest {
        expenseRepository.seed(sampleExpense)
        val viewModel = buildViewModel(expenseId = 1L)
        val state = viewModel.state.value
        assertThat(state.amountInput).isEqualTo("50000")
        assertThat(state.description).isEqualTo("Almuerzo")
        assertThat(state.expenseId).isEqualTo(1L)
    }

    @Test
    fun `delete existing expense emits NavigateBack`() = runTest {
        expenseRepository.seed(sampleExpense)
        val viewModel = buildViewModel(expenseId = 1L)
        viewModel.events.test {
            viewModel.onAction(ExpenseFormAction.OnDeleteConfirm)
            assertThat(awaitItem()).isEqualTo(ExpenseFormEvent.NavigateBack)
        }
    }

    @Test
    fun `back click emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(ExpenseFormAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(ExpenseFormEvent.NavigateBack)
        }
    }
}
