package com.software.financetracker.feature.income.recurring.list

import com.software.financetracker.domain.model.RecurrenceType

data class RecurringIncomeTemplateUi(
    val id: Long,
    val amountCop: Long,
    val source: String,
    val recurrenceType: RecurrenceType,
    val displayNextDueDate: String,
    val isActive: Boolean
)
