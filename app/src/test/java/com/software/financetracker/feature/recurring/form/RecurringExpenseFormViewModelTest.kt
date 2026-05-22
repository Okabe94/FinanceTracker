package com.software.financetracker.feature.recurring.form

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.software.financetracker.data.local.category.CategoryEntity
import com.software.financetracker.data.local.recurring.RecurringExpenseEntity
import com.software.financetracker.fake.FakeCategoryRepository
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
class RecurringExpenseFormViewModelTest {

    private lateinit var recurringRepository: FakeRecurringExpenseRepository
    private lateinit var categoryRepository: FakeCategoryRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleCategory = CategoryEntity(
        id = 1L, name = "Arriendo",
        colorArgb = 0xFF039BE5.toInt(), iconKey = "home", monthlyLimitCop = null
    )

    private val sampleRecurring = RecurringExpenseEntity(
        id = 5L, categoryId = 1L, amountCop = 800_000L,
        description = "Arriendo mensual", recurrenceType = "MONTHLY",
        startDate = "2024-01-01", nextDueDate = "2024-02-01", isActive = true
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        recurringRepository = FakeRecurringExpenseRepository()
        categoryRepository = FakeCategoryRepository()
        categoryRepository.seed(sampleCategory)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(categoryId: Long = 0L, recurringExpenseId: Long? = null): RecurringExpenseFormViewModel {
        val savedStateHandle = SavedStateHandle(
            mapOf("categoryId" to categoryId, "recurringExpenseId" to recurringExpenseId)
        )
        return RecurringExpenseFormViewModel(savedStateHandle, recurringRepository, categoryRepository)
    }

    @Test
    fun `save with invalid amount sets amountError`() = runTest {
        val viewModel = buildViewModel(categoryId = 1L)
        viewModel.onAction(RecurringExpenseFormAction.OnAmountChange("not-a-number"))
        viewModel.onAction(RecurringExpenseFormAction.OnSaveClick)
        assertThat(viewModel.state.value.amountError).isNotNull()
    }

    @Test
    fun `save with categoryId zero sets categoryError`() = runTest {
        val viewModel = buildViewModel(categoryId = 0L)
        viewModel.onAction(RecurringExpenseFormAction.OnAmountChange("500000"))
        viewModel.onAction(RecurringExpenseFormAction.OnSaveClick)
        assertThat(viewModel.state.value.categoryError).isTrue()
    }

    @Test
    fun `save valid recurring expense emits NavigateBack`() = runTest {
        val viewModel = buildViewModel(categoryId = 1L)
        viewModel.onAction(RecurringExpenseFormAction.OnAmountChange("500000"))
        viewModel.events.test {
            viewModel.onAction(RecurringExpenseFormAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(RecurringExpenseFormEvent.NavigateBack)
        }
    }

    @Test
    fun `save when repo returns error emits ShowError`() = runTest {
        recurringRepository.shouldReturnError = true
        val viewModel = buildViewModel(categoryId = 1L)
        viewModel.onAction(RecurringExpenseFormAction.OnAmountChange("500000"))
        viewModel.events.test {
            viewModel.onAction(RecurringExpenseFormAction.OnSaveClick)
            assertThat(awaitItem()).isInstanceOf(RecurringExpenseFormEvent.ShowError::class)
        }
    }

    @Test
    fun `delete recurring expense emits NavigateBack`() = runTest {
        recurringRepository.seed(sampleRecurring)
        val viewModel = buildViewModel(recurringExpenseId = 5L)
        viewModel.events.test {
            viewModel.onAction(RecurringExpenseFormAction.OnDeleteConfirm)
            assertThat(awaitItem()).isEqualTo(RecurringExpenseFormEvent.NavigateBack)
        }
    }

    @Test
    fun `back click emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(RecurringExpenseFormAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(RecurringExpenseFormEvent.NavigateBack)
        }
    }
}
