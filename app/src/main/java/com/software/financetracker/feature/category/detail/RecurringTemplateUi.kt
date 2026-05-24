package com.software.financetracker.feature.category.detail

import com.software.financetracker.domain.model.RecurrenceType

data class RecurringTemplateUi(
    val id: Long,
    val amountCop: Long,
    val description: String,
    val recurrenceType: RecurrenceType,
    val displayNextDueDate: String,
    val isActive: Boolean
)
