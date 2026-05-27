package com.software.financetracker.feature.investment.list

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.software.financetracker.data.local.investment.InvestmentEntity
import com.software.financetracker.data.local.investment.InvestmentEntryEntity
import com.software.financetracker.fake.FakeExchangeRateRepository
import com.software.financetracker.fake.FakeInvestmentEntryRepository
import com.software.financetracker.fake.FakeInvestmentRepository
import com.software.financetracker.fake.FakeUserPreferencesRepository
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
    private lateinit var exchangeRateRepository: FakeExchangeRateRepository
    private lateinit var fakePrefs: FakeUserPreferencesRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        investmentRepository = FakeInvestmentRepository()
        entryRepository = FakeInvestmentEntryRepository()
        exchangeRateRepository = FakeExchangeRateRepository()
        fakePrefs = FakeUserPreferencesRepository()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() =
        InvestmentListViewModel(investmentRepository, entryRepository, exchangeRateRepository, fakePrefs)

    // ─── helpers ────────────────────────────────────────────────────────────────

    private fun investment(id: Long, name: String = "Inv$id") = InvestmentEntity(
        id = id, name = name, currency = "COP",
        colorArgb = 0xFF000000.toInt(), iconKey = "trending_up",
        annualRatePercent = null, maturityDate = null,
        createdDate = "2024-01-01",
        targetValueMinorUnits = null, targetDate = null
    )

    private fun dividend(id: Long, investmentId: Long, amount: Long) = InvestmentEntryEntity(
        id = id, investmentId = investmentId,
        entryType = "DIVIDEND", amountMinorUnits = amount,
        date = "2024-06-01", notes = ""
    )

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

    @Test
    fun `dividendsFormatted is non-null on card when investment has dividends`() = runTest {
        investmentRepository.seed(investment(id = 1L))
        entryRepository.seed(dividend(id = 1L, investmentId = 1L, amount = 50_000L))

        val viewModel = buildViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.investments.first().dividendsFormatted).isNotNull()
        }
    }

    @Test
    fun `dividendsFormatted is null on card when investment has no dividends`() = runTest {
        investmentRepository.seed(investment(id = 1L))
        // No dividend entries seeded

        val viewModel = buildViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.investments.first().dividendsFormatted).isNull()
        }
    }

    @Test
    fun `portfolioSummary totalDividendsMinorUnits sums dividends across investments`() = runTest {
        investmentRepository.seed(investment(id = 1L, name = "A"), investment(id = 2L, name = "B"))
        entryRepository.seed(
            dividend(id = 1L, investmentId = 1L, amount = 30_000L),
            dividend(id = 2L, investmentId = 2L, amount = 20_000L)
        )

        val viewModel = buildViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.portfolioSummary?.totalDividendsMinorUnits).isEqualTo(50_000L)
        }
    }
}
