package com.software.financetracker.feature.expense.form

import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.domain.model.RecurrenceType

data class ExpenseFormState(
    val expenseId: Long? = null,
    val recurringExpenseId: Long? = null,
    val categoryId: Long? = null,
    val categories: List<CategoryItem> = emptyList(),
    val showCategoryDropdown: Boolean = false,
    val categoryError: Boolean = false,
    val amountInput: String = "",
    val amountError: UiText? = null,
    val description: String = "",
    val selectedDateStorage: String = DateUtil.today(),
    val displayDate: String = DateUtil.toDisplayDate(DateUtil.today()),
    val showDatePicker: Boolean = false,
    val isRecurring: Boolean = false,
    val recurrenceType: RecurrenceType = RecurrenceType.Monthly,
    val customIntervalDays: Int = 7,
    val isActive: Boolean = true,
    val isSaving: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false
)
