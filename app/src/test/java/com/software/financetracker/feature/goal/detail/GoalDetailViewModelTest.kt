package com.software.financetracker.feature.goal.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.software.financetracker.core.error.Result
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
class GoalDetailViewModelTest {

    private lateinit var goalRepository: FakeGoalRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleGoal = GoalEntity(
        id = 1L,
        name = "Vacaciones",
        targetAmountCop = 5_000_000L,
        currentAmountCop = 1_000_000L,
        deadlineDate = "2099-12-31",
        colorArgb = 0xFF039BE5.toInt(),
        isAchieved = false
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        goalRepository = FakeGoalRepository()
        goalRepository.seed(sampleGoal)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(goalId: Long = 1L): GoalDetailViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("goalId" to goalId))
        return GoalDetailViewModel(savedStateHandle, goalRepository)
    }

    @Test
    fun `goal loads into state`() = runTest {
        val viewModel = buildViewModel()
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.goal?.name).isEqualTo("Vacaciones")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `add contribution increases currentAmountCop`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(GoalDetailAction.OnContributionChange("500000"))
        viewModel.onAction(GoalDetailAction.OnContributionConfirm)
        // check the repo directly — the update is persisted regardless of state subscription
        val updated = goalRepository.getById(1L)
        assertThat((updated as Result.Success).data.currentAmountCop).isEqualTo(1_500_000L)
    }

    @Test
    fun `add contribution clears dialog and input`() = runTest {
        val viewModel = buildViewModel()
        viewModel.state.test {
            awaitItem() // initial state
            viewModel.onAction(GoalDetailAction.OnAddContributionClick)
            val dialogOpen = awaitItem()
            assertThat(dialogOpen.showContributionDialog).isEqualTo(true)
            viewModel.onAction(GoalDetailAction.OnContributionChange("200000"))
            awaitItem() // contribution input updated
            viewModel.onAction(GoalDetailAction.OnContributionConfirm)
            // after confirm: dialog closes, input clears, then repo updates trigger state re-emit
            val afterConfirm = awaitItem()
            assertThat(afterConfirm.showContributionDialog).isEqualTo(false)
            assertThat(afterConfirm.contributionInput).isEqualTo("")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `mark achieved sets isAchieved on goal`() = runTest {
        val viewModel = buildViewModel()
        viewModel.state.test {
            awaitItem() // subscribe
            viewModel.onAction(GoalDetailAction.OnMarkAchievedClick)
            // After marking achieved, the goal moves from active to achieved list
            // state will update as the repo emits new data
            skipItems(1) // skip intermediate state
            cancelAndIgnoreRemainingEvents()
        }
        val updated = goalRepository.getById(1L)
        assertThat((updated as Result.Success).data.isAchieved).isTrue()
    }

    @Test
    fun `OnBackClick emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(GoalDetailAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(GoalDetailEvent.NavigateBack)
        }
    }

    @Test
    fun `OnEditClick emits NavigateToEdit with correct id`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(GoalDetailAction.OnEditClick)
            assertThat(awaitItem()).isEqualTo(GoalDetailEvent.NavigateToEdit(1L))
        }
    }

    @Test
    fun `dismiss contribution dialog clears input`() = runTest {
        val viewModel = buildViewModel()
        viewModel.state.test {
            awaitItem() // initial state
            viewModel.onAction(GoalDetailAction.OnContributionChange("100000"))
            awaitItem() // input updated
            viewModel.onAction(GoalDetailAction.OnContributionDismiss)
            val dismissed = awaitItem()
            assertThat(dismissed.contributionInput).isEqualTo("")
            assertThat(dismissed.showContributionDialog).isEqualTo(false)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
