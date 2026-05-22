package com.software.financetracker.feature.investment.detail

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isGreaterThan
import com.software.financetracker.data.local.investment.InvestmentEntryEntity
import com.software.financetracker.data.local.investment.InvestmentEntity
import com.software.financetracker.domain.model.investment.EntryType
import java.time.LocalDate
import org.junit.jupiter.api.Test

class ComputeMetricsTest {

    private fun buildInvestment(
        id: Long = 1L,
        annualRatePercent: Double? = null
    ) = InvestmentEntity(
        id = id,
        name = "Test",
        currency = "COP",
        colorArgb = 0xFF039BE5.toInt(),
        iconKey = "trending_up",
        annualRatePercent = annualRatePercent,
        maturityDate = null,
        createdDate = "2024-01-01"
    )

    private fun injection(id: Long, amount: Long, date: String = "2024-01-01") =
        InvestmentEntryEntity(
            id = id,
            investmentId = 1L,
            entryType = EntryType.CASH_INJECTION.storageKey,
            amountMinorUnits = amount,
            date = date
        )

    private fun snapshot(id: Long, amount: Long, date: String = "2024-06-01") =
        InvestmentEntryEntity(
            id = id,
            investmentId = 1L,
            entryType = EntryType.VALUE_SNAPSHOT.storageKey,
            amountMinorUnits = amount,
            date = date
        )

    private fun withdrawal(id: Long, amount: Long, date: String = "2024-07-01") =
        InvestmentEntryEntity(
            id = id,
            investmentId = 1L,
            entryType = EntryType.WITHDRAWAL.storageKey,
            amountMinorUnits = amount,
            date = date
        )

    private fun dividend(id: Long, amount: Long, date: String = "2024-06-15") =
        InvestmentEntryEntity(
            id = id,
            investmentId = 1L,
            entryType = EntryType.DIVIDEND.storageKey,
            amountMinorUnits = amount,
            date = date
        )

    @Test
    fun `cash injection only - currentValue equals totalInvested`() {
        val investment = buildInvestment()
        val entries = listOf(injection(id = 1, amount = 1_000_000L))

        val metrics = computeMetrics(investment, entries)

        assertThat(metrics.totalInvestedMinorUnits).isEqualTo(1_000_000L)
        assertThat(metrics.currentValueMinorUnits).isEqualTo(1_000_000L)
        assertThat(metrics.returnMinorUnits).isEqualTo(0L)
    }

    @Test
    fun `value snapshot overrides currentValue`() {
        val investment = buildInvestment()
        val entries = listOf(
            injection(id = 1, amount = 1_000_000L, date = "2024-01-01"),
            snapshot(id = 2, amount = 1_200_000L, date = "2024-06-01")
        )

        val metrics = computeMetrics(investment, entries)

        assertThat(metrics.currentValueMinorUnits).isEqualTo(1_200_000L)
        assertThat(metrics.totalInvestedMinorUnits).isEqualTo(1_000_000L)
        assertThat(metrics.returnMinorUnits).isEqualTo(200_000L)
    }

    @Test
    fun `withdrawal after snapshot reduces currentValue`() {
        val investment = buildInvestment()
        val entries = listOf(
            injection(id = 1, amount = 2_000_000L, date = "2024-01-01"),
            snapshot(id = 2, amount = 2_500_000L, date = "2024-06-01"),
            withdrawal(id = 3, amount = 500_000L, date = "2024-07-01")
        )

        val metrics = computeMetrics(investment, entries)

        // snapshot was 2_500_000, withdrawal of 500_000 after snapshot -> currentValue = 2_000_000
        assertThat(metrics.currentValueMinorUnits).isEqualTo(2_000_000L)
    }

    @Test
    fun `fixed ROI with no snapshot grows currentValue beyond totalInvested`() {
        val investment = buildInvestment(annualRatePercent = 12.0)
        // Use a past date so days elapsed > 0
        val pastDate = LocalDate.now().minusYears(1).toString()
        val entries = listOf(injection(id = 1, amount = 1_000_000L, date = pastDate))

        val metrics = computeMetrics(investment, entries)

        assertThat(metrics.currentValueMinorUnits).isGreaterThan(1_000_000L)
        assertThat(metrics.returnPercent).isNotNull()
    }

    @Test
    fun `withdrawal reduces totalInvested`() {
        val investment = buildInvestment()
        val entries = listOf(
            injection(id = 1, amount = 1_000_000L, date = "2024-01-01"),
            withdrawal(id = 2, amount = 300_000L, date = "2024-03-01")
        )

        val metrics = computeMetrics(investment, entries)

        assertThat(metrics.totalInvestedMinorUnits).isEqualTo(700_000L)
    }

    @Test
    fun `dividends are summed independently`() {
        val investment = buildInvestment()
        val entries = listOf(
            injection(id = 1, amount = 1_000_000L, date = "2024-01-01"),
            dividend(id = 2, amount = 50_000L, date = "2024-03-01"),
            dividend(id = 3, amount = 75_000L, date = "2024-06-01")
        )

        val metrics = computeMetrics(investment, entries)

        assertThat(metrics.dividendsTotalMinorUnits).isEqualTo(125_000L)
    }

    @Test
    fun `returnPercent is null when no investment`() {
        val investment = buildInvestment()
        val metrics = computeMetrics(investment, emptyList())

        assertThat(metrics.returnPercent).isNull()
        assertThat(metrics.totalInvestedMinorUnits).isEqualTo(0L)
    }

    @Test
    fun `returnPercent is positive when currentValue exceeds totalInvested`() {
        val investment = buildInvestment()
        val entries = listOf(
            injection(id = 1, amount = 1_000_000L, date = "2024-01-01"),
            snapshot(id = 2, amount = 1_500_000L, date = "2024-06-01")
        )

        val metrics = computeMetrics(investment, entries)

        assertThat(metrics.returnPercent).isNotNull()
        val percent = metrics.returnPercent!!
        assertThat(percent).isGreaterThan(0f)
        assertThat(percent).isEqualTo(50f)
    }
}
