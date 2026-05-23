package com.software.financetracker.feature.income.recurring.form

import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.domain.model.RecurrenceType
import com.software.financetracker.feature.income.IncomeSourceType

data class RecurringIncomeFormState(
    val recurringIncomeId: Long? = null,
    val amountInput: String = "",
    val amountError: UiText? = null,
    val selectedSourceType: IncomeSourceType = IncomeSourceType.SALARY,
    val customSource: String = "",
    val showSourceDropdown: Boolean = false,
    val notes: String = "",
    val recurrenceType: RecurrenceType = RecurrenceType.Monthly,
    val selectedDateStorage: String = DateUtil.today(),
    val displayDate: String = DateUtil.toDisplayDate(DateUtil.today()),
    val showDatePicker: Boolean = false,
    val isActive: Boolean = true,
    val showDeleteConfirmDialog: Boolean = false,
    val isSaving: Boolean = false
)
