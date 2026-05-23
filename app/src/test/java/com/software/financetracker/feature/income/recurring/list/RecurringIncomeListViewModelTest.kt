package com.software.financetracker.feature.income.recurring.list

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
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
class RecurringIncomeListViewModelTest {

    private lateinit var repository: FakeRecurringIncomeRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleTemplate = RecurringIncomeEntity(
        id = 3L,
        amountCop = 3_000_000L,
        source = "Salario",
        notes = "",
        recurrenceType = "MONTHLY",
        startDate = "2024-01-01",
        nextDueDate = "2024-06-01",
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

    private fun buildViewModel() = RecurringIncomeListViewModel(repository)

    @Test
    fun `back click emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(RecurringIncomeListAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(RecurringIncomeListEvent.NavigateBack)
        }
    }

    @Test
    fun `add click emits NavigateToAddForm`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(RecurringIncomeListAction.OnAddClick)
            assertThat(awaitItem()).isEqualTo(RecurringIncomeListEvent.NavigateToAddForm)
        }
    }

    @Test
    fun `template click emits NavigateToEditForm with correct id`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(RecurringIncomeListAction.OnTemplateClick(templateId = 3L))
            val event = awaitItem()
            assertThat(event).isInstanceOf(RecurringIncomeListEvent.NavigateToEditForm::class)
            assertThat((event as RecurringIncomeListEvent.NavigateToEditForm).templateId).isEqualTo(3L)
        }
    }

    @Test
    fun `state maps active recurring income templates`() = runTest {
        repository.seed(sampleTemplate)
        val viewModel = buildViewModel()
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.templates).hasSize(1)
            val template = state.templates.first()
            assertThat(template.id).isEqualTo(3L)
            assertThat(template.source).isEqualTo("Salario")
            assertThat(template.amountCop).isEqualTo(3_000_000L)
            assertThat(template.isActive).isEqualTo(true)
        }
    }

    @Test
    fun `inactive templates are excluded from state`() = runTest {
        repository.seed(sampleTemplate.copy(id = 10L, isActive = false))
        val viewModel = buildViewModel()
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.templates).hasSize(0)
        }
    }
}
