package com.software.financetracker.feature.category.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import com.software.financetracker.data.local.category.CategoryEntity
import com.software.financetracker.data.local.recurring.RecurringExpenseEntity
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
class CategoryDetailViewModelTest {

    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var expenseRepository: FakeExpenseRepository
    private lateinit var recurringExpenseRepository: FakeRecurringExpenseRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val categoryId = 1L
    private val sampleCategory = CategoryEntity(
        id = categoryId,
        name = "Alimentación",
        colorArgb = 0xFF33B679.toInt(),
        iconKey = "restaurant",
        monthlyLimitCop = null
    )

    private val sampleRecurring = RecurringExpenseEntity(
        id = 5L,
        categoryId = categoryId,
        amountCop = 80_000L,
        description = "Gimnasio",
        recurrenceType = "MONTHLY",
        startDate = "2024-01-01",
        nextDueDate = "2024-02-01",
        isActive = true
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        categoryRepository = FakeCategoryRepository()
        expenseRepository = FakeExpenseRepository()
        recurringExpenseRepository = FakeRecurringExpenseRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(catId: Long = categoryId): CategoryDetailViewModel {
        val savedStateHandle = SavedStateHandle(
            mapOf("categoryId" to catId, "selectedMonth" to "2024-01")
        )
        return CategoryDetailViewModel(savedStateHandle, categoryRepository, expenseRepository, recurringExpenseRepository)
    }

    @Test
    fun `givenCategoryWithRecurring_emitsRecurringSection`() = runTest {
        categoryRepository.seed(sampleCategory)
        recurringExpenseRepository.seed(sampleRecurring)
        val viewModel = buildViewModel()
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.recurringExpenses).isNotEmpty()
            assertThat(state.recurringExpenses.first().id).isEqualTo(5L)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `givenNoRecurring_emitsEmptyRecurringSection`() = runTest {
        categoryRepository.seed(sampleCategory)
        val viewModel = buildViewModel()
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.recurringExpenses).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRecurringExpenseClick_emitsNavigateToExpenseFormWithRecurringId`() = runTest {
        categoryRepository.seed(sampleCategory)
        recurringExpenseRepository.seed(sampleRecurring)
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(CategoryDetailAction.OnRecurringExpenseClick(5L))
            val event = awaitItem()
            assertThat(event).isInstanceOf(CategoryDetailEvent.NavigateToExpenseForm::class)
            val navEvent = event as CategoryDetailEvent.NavigateToExpenseForm
            assertThat(navEvent.recurringExpenseId).isEqualTo(5L)
            assertThat(navEvent.categoryId).isEqualTo(categoryId)
        }
    }
}
