package com.software.financetracker.feature.recurring.list

import com.software.financetracker.domain.model.RecurrenceType

data class RecurringListState(
    val templates: List<RecurringTemplateUi> = emptyList(),
    val categories: List<CategoryPickerItem> = emptyList(),
    val isLoading: Boolean = true
)

data class RecurringTemplateUi(
    val id: Long,
    val categoryName: String,
    val categoryColorArgb: Int,
    val categoryIconKey: String,
    val amountCop: Long,
    val description: String,
    val recurrenceType: RecurrenceType,
    val displayNextDueDate: String,
    val isActive: Boolean
)

data class CategoryPickerItem(
    val id: Long,
    val name: String,
    val iconKey: String,
    val colorArgb: Int
)
