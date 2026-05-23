package com.software.financetracker.feature.investment.form

sealed interface InvestmentFormAction {
    data object OnBackClick : InvestmentFormAction
    data class OnNameChange(val value: String) : InvestmentFormAction
    data class OnColorSelected(val argb: Int) : InvestmentFormAction
    data class OnIconSelected(val key: String) : InvestmentFormAction
    data class OnCurrencySelected(val currency: String) : InvestmentFormAction
    data object OnCurrencyDropdownToggle : InvestmentFormAction
    data class OnFixedRoiToggle(val enabled: Boolean) : InvestmentFormAction
    data class OnAnnualRateChange(val value: String) : InvestmentFormAction
    data object OnMaturityDateClick : InvestmentFormAction
    data class OnMaturityDateSelected(val dateMillis: Long) : InvestmentFormAction
    data object OnMaturityDatePickerDismiss : InvestmentFormAction
    data object OnSaveClick : InvestmentFormAction
    data object OnDeleteClick : InvestmentFormAction
    data object OnDeleteConfirm : InvestmentFormAction
    data object OnDeleteDismiss : InvestmentFormAction
    data object OnTargetEnabledToggled : InvestmentFormAction
    data class OnTargetValueChanged(val value: String) : InvestmentFormAction
    data object OnTargetDateClick : InvestmentFormAction
    data class OnTargetDateSelected(val dateMillis: Long) : InvestmentFormAction
    data object OnTargetDatePickerDismiss : InvestmentFormAction
}
