package com.software.financetracker.feature.expense.assistant

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.software.financetracker.data.local.category.CategoryEntity
import com.software.financetracker.fake.FakeCategoryRepository
import com.software.financetracker.fake.FakeExpenseRepository
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
class AssistantExpenseViewModelTest {

    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var expenseRepository: FakeExpenseRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleCategory = CategoryEntity(
        id = 1L,
        name = "Comida",
        colorArgb = 0xFF039BE5.toInt(),
        iconKey = "restaurant",
        monthlyLimitCop = null
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        categoryRepository = FakeCategoryRepository()
        expenseRepository = FakeExpenseRepository()
        categoryRepository.seed(sampleCategory)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(categoryName: String = "", amountCop: Long = 0L): AssistantExpenseViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("categoryName" to categoryName, "amountCop" to amountCop))
        return AssistantExpenseViewModel(savedStateHandle, categoryRepository, expenseRepository)
    }

    @Test
    fun `when categoryName matches a category, selectedCategory is set in state`() = runTest {
        val viewModel = buildViewModel(categoryName = "Comida")
        assertThat(viewModel.state.value.selectedCategory).isEqualTo(sampleCategory)
    }

    @Test
    fun `category matching is case-insensitive`() = runTest {
        val viewModel = buildViewModel(categoryName = "comida")
        assertThat(viewModel.state.value.selectedCategory).isEqualTo(sampleCategory)
    }

    @Test
    fun `when categoryName is blank, selectedCategory is null`() = runTest {
        val viewModel = buildViewModel(categoryName = "")
        assertThat(viewModel.state.value.selectedCategory).isNull()
    }

    @Test
    fun `when amountCop from route is positive, amountInput is pre-filled`() = runTest {
        val viewModel = buildViewModel(amountCop = 50000L)
        assertThat(viewModel.state.value.amountInput).isEqualTo("50000")
    }

    @Test
    fun `save without selecting a category sets categoryError in state`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(AssistantExpenseAction.OnAmountChange("10000"))
        viewModel.onAction(AssistantExpenseAction.OnSaveClick)
        assertThat(viewModel.state.value.categoryError).isNotNull()
    }

    @Test
    fun `save without a valid amount sets amountError in state`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(AssistantExpenseAction.OnCategorySelected(sampleCategory))
        viewModel.onAction(AssistantExpenseAction.OnAmountChange("abc"))
        viewModel.onAction(AssistantExpenseAction.OnSaveClick)
        assertThat(viewModel.state.value.amountError).isNotNull()
    }

    @Test
    fun `save with valid category and amount emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onAction(AssistantExpenseAction.OnCategorySelected(sampleCategory))
        viewModel.onAction(AssistantExpenseAction.OnAmountChange("20000"))
        viewModel.events.test {
            viewModel.onAction(AssistantExpenseAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(AssistantExpenseEvent.NavigateBack)
        }
    }

    @Test
    fun `save when repository returns error emits ShowError`() = runTest {
        expenseRepository.shouldReturnError = true
        val viewModel = buildViewModel()
        viewModel.onAction(AssistantExpenseAction.OnCategorySelected(sampleCategory))
        viewModel.onAction(AssistantExpenseAction.OnAmountChange("20000"))
        viewModel.events.test {
            viewModel.onAction(AssistantExpenseAction.OnSaveClick)
            assertThat(awaitItem()).isInstanceOf(AssistantExpenseEvent.ShowError::class)
        }
    }

    @Test
    fun `back click emits NavigateBack`() = runTest {
        val viewModel = buildViewModel()
        viewModel.events.test {
            viewModel.onAction(AssistantExpenseAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(AssistantExpenseEvent.NavigateBack)
        }
    }
}
