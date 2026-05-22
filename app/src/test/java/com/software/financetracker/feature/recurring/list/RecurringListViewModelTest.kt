package com.software.financetracker.feature.recurring.list

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecurringListViewModelTest {

    private lateinit var recurringRepository: FakeRecurringExpenseRepository
    private lateinit var categoryRepository: FakeCategoryRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleCategory = CategoryEntity(
        id = 1L,
        name = "Netflix",
        colorArgb = 0xFFE53935.toInt(),
        iconKey = "subscriptions",
        monthlyLimitCop = null
    )

    private val sampleRecurring = RecurringExpenseEntity(
        id = 3L,
        categoryId = 1L,
        amountCop = 49_900L,
        description = "Suscripción Netflix",
        recurrenceType = "MONTHLY",
        startDate = "2024-01-01",
        nextDueDate = "2024-06-01",
        isActive = true
    )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        recurringRepository = FakeRecurringExpenseRepository()
        categoryRepository = FakeCategoryRepository()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() =
        RecurringListViewModel(recurringRepository, categoryRepository)

    @Test
    fun `back click emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()

        viewModel.events.test {
            viewModel.onAction(RecurringListAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(RecurringListEvent.NavigateBack)
        }
    }

    @Test
    fun `add click emits NavigateToAddForm`() = runTest {
        val viewModel = buildViewModel()

        viewModel.events.test {
            viewModel.onAction(RecurringListAction.OnAddClick)
            assertThat(awaitItem()).isEqualTo(RecurringListEvent.NavigateToAddForm)
        }
    }

    @Test
    fun `template click emits NavigateToEditForm with correct id`() = runTest {
        val viewModel = buildViewModel()

        viewModel.events.test {
            viewModel.onAction(RecurringListAction.OnTemplateClick(templateId = 3L))
            val event = awaitItem()
            assertThat(event).isInstanceOf(RecurringListEvent.NavigateToEditForm::class)
            assertThat((event as RecurringListEvent.NavigateToEditForm).templateId).isEqualTo(3L)
        }
    }

    @Test
    fun `state maps recurring expenses with category info`() = runTest {
        categoryRepository.seed(sampleCategory)
        recurringRepository.seed(sampleRecurring)
        val viewModel = buildViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.templates).hasSize(1)
            val template = state.templates.first()
            assertThat(template.id).isEqualTo(3L)
            assertThat(template.categoryName).isEqualTo("Netflix")
            assertThat(template.amountCop).isEqualTo(49_900L)
            assertThat(template.isActive).isEqualTo(true)
        }
    }
}
