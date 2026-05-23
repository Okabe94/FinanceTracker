package com.software.financetracker.feature.income.form

import com.software.financetracker.feature.income.IncomeSourceType

sealed interface IncomeFormAction {
    data object OnBackClick : IncomeFormAction
    data class OnAmountChange(val value: String) : IncomeFormAction
    data class OnSourceTypeSelected(val type: IncomeSourceType) : IncomeFormAction
    data object OnSourceDropdownToggle : IncomeFormAction
    data object OnSourceDropdownDismiss : IncomeFormAction
    data class OnCustomSourceChange(val value: String) : IncomeFormAction
    data class OnNotesChange(val value: String) : IncomeFormAction
    data object OnDateFieldClick : IncomeFormAction
    data class OnDateSelected(val epochMillis: Long) : IncomeFormAction
    data object OnDatePickerDismiss : IncomeFormAction
    data object OnSaveClick : IncomeFormAction
    data object OnDeleteClick : IncomeFormAction
    data object OnDeleteConfirm : IncomeFormAction
    data object OnDeleteDismiss : IncomeFormAction
}
