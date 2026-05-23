package com.software.financetracker.feature.income.list

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import com.software.financetracker.data.local.income.IncomeEntity
import com.software.financetracker.fake.FakeIncomeRepository
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
class IncomeListViewModelTest {

    private lateinit var incomeRepository: FakeIncomeRepository
    private lateinit var recurringIncomeRepository: FakeRecurringIncomeRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleIncome = IncomeEntity(
        id = 1L,
        amountCop = 3_000_000L,
        source = "Salario",
        date = "2024-01-15"
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

    private fun buildViewModel(): IncomeListViewModel =
        IncomeListViewModel(incomeRepository, recurringIncomeRepository)

    @Test
    fun `state is not loading after repo emits`() = runTest {
        val viewModel = buildViewModel()
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `empty repos show empty list`() = runTest {
        val viewModel = buildViewModel()
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.items).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `seeded entries appear in state`() = runTest {
        incomeRepository.seed(sampleIncome)
        val viewModel = buildViewModel()
        viewModel.state.test {
            val state = awaitItem()
            val entries = state.items.filterIsInstance<IncomeItem.Entry>()
            assertThat(entries).isNotEmpty()
            assertThat(entries.first().source).isEqualTo("Salario")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnBackClick emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(IncomeListAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(IncomeListEvent.NavigateBack)
        }
    }

    @Test
    fun `OnAddIncomeClick emits NavigateToAddIncome`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(IncomeListAction.OnAddIncomeClick)
            assertThat(awaitItem()).isEqualTo(IncomeListEvent.NavigateToAddIncome)
        }
    }

    @Test
    fun `OnAddTemplateClick emits NavigateToAddTemplate`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(IncomeListAction.OnAddTemplateClick)
            assertThat(awaitItem()).isEqualTo(IncomeListEvent.NavigateToAddTemplate)
        }
    }

    @Test
    fun `OnEntryClick emits NavigateToEditIncome with correct id`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(IncomeListAction.OnEntryClick(42L))
            assertThat(awaitItem()).isEqualTo(IncomeListEvent.NavigateToEditIncome(42L))
        }
    }

    @Test
    fun `OnTemplateClick emits NavigateToEditTemplate with correct id`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(IncomeListAction.OnTemplateClick(7L))
            assertThat(awaitItem()).isEqualTo(IncomeListEvent.NavigateToEditTemplate(7L))
        }
    }

    @Test
    fun `OnDeleteClick removes entry from list`() = runTest {
        incomeRepository.seed(sampleIncome)
        val viewModel = buildViewModel()
        viewModel.state.test {
            awaitItem() // initial emission with data
            viewModel.onAction(IncomeListAction.OnDeleteClick(1L))
            val updated = awaitItem()
            assertThat(updated.items.filterIsInstance<IncomeItem.Entry>()).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
