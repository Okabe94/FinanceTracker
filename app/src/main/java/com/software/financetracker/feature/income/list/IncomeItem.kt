package com.software.financetracker.feature.income.list

sealed interface IncomeItem {
    data class Entry(
        val id: Long,
        val amountCop: Long,
        val source: String,
        val displayDate: String,
        val notes: String,
        val isFromTemplate: Boolean
    ) : IncomeItem

    data class Template(
        val id: Long,
        val amountCop: Long,
        val source: String,
        val recurrenceLabel: String,
        val displayNextDueDate: String
    ) : IncomeItem
}
