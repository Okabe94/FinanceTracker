package com.software.financetracker.feature.goal.list

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import com.software.financetracker.data.local.goal.GoalEntity
import com.software.financetracker.fake.FakeGoalRepository
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
class GoalListViewModelTest {

    private lateinit var goalRepository: FakeGoalRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val activeGoal = GoalEntity(
        id = 1L,
        name = "Vacaciones",
        targetAmountCop = 5_000_000L,
        currentAmountCop = 0L,
        deadlineDate = "2099-12-31",
        colorArgb = 0xFF039BE5.toInt(),
        isAchieved = false
    )

    private val achievedGoal = GoalEntity(
        id = 2L,
        name = "Fondo emergencia",
        targetAmountCop = 2_000_000L,
        currentAmountCop = 2_000_000L,
        deadlineDate = "2024-06-01",
        colorArgb = 0xFF43A047.toInt(),
        isAchieved = true
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        goalRepository = FakeGoalRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(): GoalListViewModel =
        GoalListViewModel(goalRepository)

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
    fun `empty repo shows empty active and achieved lists`() = runTest {
        val viewModel = buildViewModel()
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.activeGoals).isEmpty()
            assertThat(state.achievedGoals).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `active goals appear in activeGoals`() = runTest {
        goalRepository.seed(activeGoal)
        val viewModel = buildViewModel()
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.activeGoals).isNotEmpty()
            assertThat(state.achievedGoals).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `achieved goals appear in achievedGoals`() = runTest {
        goalRepository.seed(achievedGoal)
        val viewModel = buildViewModel()
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.achievedGoals).isNotEmpty()
            assertThat(state.activeGoals).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnBackClick emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(GoalListAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(GoalListEvent.NavigateBack)
        }
    }

    @Test
    fun `OnAddClick emits NavigateToAddForm`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(GoalListAction.OnAddClick)
            assertThat(awaitItem()).isEqualTo(GoalListEvent.NavigateToAddForm)
        }
    }

    @Test
    fun `OnGoalClick emits NavigateToDetail with correct id`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(GoalListAction.OnGoalClick(99L))
            assertThat(awaitItem()).isEqualTo(GoalListEvent.NavigateToDetail(99L))
        }
    }
}
