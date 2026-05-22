package com.software.financetracker.feature.investment.list

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.software.financetracker.data.local.investment.InvestmentEntity
import com.software.financetracker.fake.FakeInvestmentEntryRepository
import com.software.financetracker.fake.FakeInvestmentRepository
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
class InvestmentListViewModelTest {

    private lateinit var investmentRepository: FakeInvestmentRepository
    private lateinit var entryRepository: FakeInvestmentEntryRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        investmentRepository = FakeInvestmentRepository()
        entryRepository = FakeInvestmentEntryRepository()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() =
        InvestmentListViewModel(investmentRepository, entryRepository)

    @Test
    fun `add click emits NavigateToAddForm`() = runTest {
        val viewModel = buildViewModel()

        viewModel.events.test {
            viewModel.onAction(InvestmentListAction.OnAddClick)
            assertThat(awaitItem()).isEqualTo(InvestmentListEvent.NavigateToAddForm)
        }
    }

    @Test
    fun `card click emits NavigateToDetail with correct investmentId`() = runTest {
        val viewModel = buildViewModel()

        viewModel.events.test {
            viewModel.onAction(InvestmentListAction.OnCardClick(investmentId = 42L))
            val event = awaitItem()
            assertThat(event).isInstanceOf(InvestmentListEvent.NavigateToDetail::class)
            assertThat((event as InvestmentListEvent.NavigateToDetail).investmentId).isEqualTo(42L)
        }
    }

    @Test
    fun `state has empty list when no investments`() = runTest {
        val viewModel = buildViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.investments).isEmpty()
        }
    }
}
