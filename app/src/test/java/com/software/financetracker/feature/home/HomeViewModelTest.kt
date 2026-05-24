package com.software.financetracker.feature.home

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import com.software.financetracker.data.local.goal.GoalEntity
import com.software.financetracker.fake.FakeCategoryRepository
import com.software.financetracker.fake.FakeExpenseRepository
import com.software.financetracker.fake.FakeGoalRepository
import com.software.financetracker.fake.FakeIncomeRepository
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
class HomeViewModelTest {

    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var expenseRepository: FakeExpenseRepository
    private lateinit var incomeRepository: FakeIncomeRepository
    private lateinit var goalRepository: FakeGoalRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleGoal = GoalEntity(
        id = 1L,
        name = "Vacaciones",
        targetAmountCop = 5_000_000L,
        currentAmountCop = 1_000_000L,
        deadlineDate = "2030-12-31",
        colorArgb = 0xFF4CAF50.toInt(),
        isAchieved = false
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        categoryRepository = FakeCategoryRepository()
        expenseRepository = FakeExpenseRepository()
        incomeRepository = FakeIncomeRepository()
        goalRepository = FakeGoalRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(): HomeViewModel =
        HomeViewModel(categoryRepository, expenseRepository, incomeRepository, goalRepository)

    @Test
    fun `givenActiveGoals_emitsGoalsInState`() = runTest {
        goalRepository.seed(sampleGoal)
        val viewModel = buildViewModel()
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.activeGoals).isNotEmpty()
            assertThat(state.hasGoals).isTrue()
            assertThat(state.activeGoals.first().name).isEqualTo("Vacaciones")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `givenNoGoals_hasGoalsFalse`() = runTest {
        val viewModel = buildViewModel()
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.hasGoals).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onGoalCardClick_emitsNavigateToGoalDetail`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(HomeAction.OnGoalCardClick(42L))
            val event = awaitItem()
            assertThat(event).isEqualTo(HomeEvent.NavigateToGoalDetail(42L))
        }
    }

    @Test
    fun `onViewAllGoalsClick_emitsNavigateToGoalList`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(HomeAction.OnViewAllGoalsClick)
            assertThat(awaitItem()).isEqualTo(HomeEvent.NavigateToGoalList)
        }
    }

    @Test
    fun `onAddGoalClick_emitsNavigateToAddGoal`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(HomeAction.OnAddGoalClick)
            assertThat(awaitItem()).isEqualTo(HomeEvent.NavigateToAddGoal)
        }
    }

    @Test
    fun `onIncomeCardClick_emitsNavigateToIncomeList`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(HomeAction.OnIncomeCardClick)
            assertThat(awaitItem()).isEqualTo(HomeEvent.NavigateToIncomeList)
        }
    }

    @Test
    fun `onAddExpenseClick_emitsNavigateToAddExpense`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(HomeAction.OnAddExpenseClick)
            assertThat(awaitItem()).isEqualTo(HomeEvent.NavigateToAddExpense)
        }
    }

    @Test
    fun `onAddIncomeClick_emitsNavigateToAddIncome`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(HomeAction.OnAddIncomeClick)
            assertThat(awaitItem()).isEqualTo(HomeEvent.NavigateToAddIncome)
        }
    }
}
