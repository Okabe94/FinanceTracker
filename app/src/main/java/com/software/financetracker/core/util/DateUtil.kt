package com.software.financetracker.core.util

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtil {
    private val storageFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es"))

    fun today(): String = LocalDate.now().format(storageFormatter)

    fun currentYearMonth(): String = YearMonth.now().toString()

    fun toDisplayDate(stored: String): String =
        LocalDate.parse(stored, storageFormatter).format(displayFormatter)

    fun toStorageDate(displayed: String): String =
        LocalDate.parse(displayed, displayFormatter).format(storageFormatter)

    fun formatMonth(yearMonth: String): String =
        YearMonth.parse(yearMonth).atDay(1).format(monthFormatter)
            .replaceFirstChar { it.uppercase() }

    fun previousMonth(yearMonth: String): String =
        YearMonth.parse(yearMonth).minusMonths(1).toString()

    fun nextMonth(yearMonth: String): String =
        YearMonth.parse(yearMonth).plusMonths(1).toString()

    fun shortMonthLabel(yearMonth: String): String {
        val formatter = DateTimeFormatter.ofPattern("MMM", Locale("es"))
        return YearMonth.parse(yearMonth).atDay(1).format(formatter)
            .replaceFirstChar { it.uppercase() }
            .take(3)
    }

    fun rangeForMonthsBack(monthsBack: Int): Pair<String, String> {
        val end = YearMonth.now()
        val start = end.minusMonths(monthsBack.toLong())
        return start.atDay(1).format(storageFormatter) to end.atEndOfMonth().format(storageFormatter)
    }

    fun expensesInMonth(date: String, yearMonth: String): Boolean {
        val ym = YearMonth.parse(yearMonth)
        val d = LocalDate.parse(date, storageFormatter)
        return d.year == ym.year && d.monthValue == ym.monthValue
    }
}
