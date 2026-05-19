package com.software.financetracker.feature.recurring.form

import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.domain.model.RecurrenceType

data class RecurringExpenseFormState(
    val recurringExpenseId: Long? = null,
    val categoryId: Long = 0L,
    val amountInput: String = "",
    val amountError: UiText? = null,
    val description: String = "",
    val recurrenceType: RecurrenceType = RecurrenceType.Monthly,
    val selectedDateStorage: String = DateUtil.today(),
    val displayDate: String = DateUtil.toDisplayDate(DateUtil.today()),
    val isActive: Boolean = true,
    val showDatePicker: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val isSaving: Boolean = false,
    val categories: List<CategoryItem> = emptyList(),
    val showCategoryDropdown: Boolean = false,
    val categoryError: Boolean = false
)

data class CategoryItem(val id: Long, val name: String, val colorArgb: Int, val iconKey: String)
