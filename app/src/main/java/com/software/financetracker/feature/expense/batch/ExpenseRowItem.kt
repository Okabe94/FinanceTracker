package com.software.financetracker.feature.expense.batch

import com.software.financetracker.core.presentation.UiText

data class ExpenseRowItem(
    val rowId: Int,
    val categoryId: Long? = null,
    val categoryError: Boolean = false,
    val amountInput: String = "",
    val amountError: UiText? = null,
    val description: String = "",
    val dateStorage: String = "",
    val displayDate: String = "",
    val showDatePicker: Boolean = false
)
