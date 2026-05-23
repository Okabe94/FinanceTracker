package com.software.financetracker.feature.income.recurring.form

import com.software.financetracker.domain.model.RecurrenceType
import com.software.financetracker.feature.income.IncomeSourceType

sealed interface RecurringIncomeFormAction {
    data object OnBackClick : RecurringIncomeFormAction
    data class OnAmountChange(val value: String) : RecurringIncomeFormAction
    data class OnSourceTypeSelected(val type: IncomeSourceType) : RecurringIncomeFormAction
    data object OnSourceDropdownToggle : RecurringIncomeFormAction
    data object OnSourceDropdownDismiss : RecurringIncomeFormAction
    data class OnCustomSourceChange(val value: String) : RecurringIncomeFormAction
    data class OnNotesChange(val value: String) : RecurringIncomeFormAction
    data class OnRecurrenceTypeChange(val type: RecurrenceType) : RecurringIncomeFormAction
    data object OnDateFieldClick : RecurringIncomeFormAction
    data class OnDateSelected(val epochMillis: Long) : RecurringIncomeFormAction
    data object OnDatePickerDismiss : RecurringIncomeFormAction
    data class OnActiveToggle(val isActive: Boolean) : RecurringIncomeFormAction
    data object OnSaveClick : RecurringIncomeFormAction
    data object OnDeleteClick : RecurringIncomeFormAction
    data object OnDeleteConfirm : RecurringIncomeFormAction
    data object OnDeleteDismiss : RecurringIncomeFormAction
}
