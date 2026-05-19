package com.software.financetracker.feature.expense.form

import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.core.util.DateUtil

data class ExpenseFormState(
    val expenseId: Long? = null,
    val categoryId: Long = 0L,
    val amountInput: String = "",
    val amountError: UiText? = null,
    val description: String = "",
    val selectedDateStorage: String = DateUtil.today(),
    val displayDate: String = DateUtil.toDisplayDate(DateUtil.today()),
    val showDatePicker: Boolean = false,
    val isSaving: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false
)
