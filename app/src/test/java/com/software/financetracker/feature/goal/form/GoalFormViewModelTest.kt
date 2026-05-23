package com.software.financetracker.feature.goal.form

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
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
class GoalFormViewModelTest {

    private lateinit var goalRepository: FakeGoalRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleGoal = GoalEntity(
        id = 1L,
        name = "Vacaciones",
        targetAmountCop = 5_000_000L,
        currentAmountCop = 0L,
        deadlineDate = "2025-12-31",
        colorArgb = 0xFF039BE5.toInt(),
        isAchieved = false
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

    private fun buildViewModel(goalId: Long? = null): GoalFormViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("goalId" to goalId))
        return GoalFormViewModel(savedStateHandle, goalRepository)
    }

    @Test
    fun `save with blank name sets nameError`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(GoalFormAction.OnNameChange(""))
        viewModel.onAction(GoalFormAction.OnSaveClick)
        assertThat(viewModel.state.value.nameError).isTrue()
    }

    @Test
    fun `save with invalid amount sets targetAmountError`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(GoalFormAction.OnNameChange("Vacaciones"))
        viewModel.onAction(GoalFormAction.OnTargetAmountChange(""))
        viewModel.onAction(GoalFormAction.OnSaveClick)
        assertThat(viewModel.state.value.targetAmountError).isEqualTo(
            com.software.financetracker.core.presentation.UiText.DynamicString("Ingresa un monto válido en pesos")
        )
    }

    @Test
    fun `save valid goal emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(GoalFormAction.OnNameChange("Vacaciones"))
        viewModel.onAction(GoalFormAction.OnTargetAmountChange("5000000"))
        viewModel.events.test {
            viewModel.onAction(GoalFormAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(GoalFormEvent.NavigateBack)
        }
    }

    @Test
    fun `save when repo returns error emits ShowError`() = runTest {
        goalRepository.shouldReturnError = true
        val viewModel = buildViewModel()
        viewModel.onAction(GoalFormAction.OnNameChange("Vacaciones"))
        viewModel.onAction(GoalFormAction.OnTargetAmountChange("5000000"))
        viewModel.events.test {
            viewModel.onAction(GoalFormAction.OnSaveClick)
            assertThat(awaitItem()).isInstanceOf(GoalFormEvent.ShowError::class)
        }
    }

    @Test
    fun `delete existing goal emits NavigateBack`() = runTest {
        goalRepository.seed(sampleGoal)
        val viewModel = buildViewModel(goalId = 1L)
        viewModel.events.test {
            viewModel.onAction(GoalFormAction.OnDeleteConfirm)
            assertThat(awaitItem()).isEqualTo(GoalFormEvent.NavigateBack)
        }
    }

    @Test
    fun `back click emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(GoalFormAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(GoalFormEvent.NavigateBack)
        }
    }

    @Test
    fun `loading existing goal populates state fields`() = runTest {
        goalRepository.seed(sampleGoal)
        val viewModel = buildViewModel(goalId = 1L)
        val state = viewModel.state.value
        assertThat(state.name).isEqualTo("Vacaciones")
        assertThat(state.targetAmountInput).isEqualTo("5000000")
        assertThat(state.goalId).isEqualTo(1L)
    }
}
