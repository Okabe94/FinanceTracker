package com.software.financetracker.feature.category.form

sealed interface CategoryFormAction {
    data object OnBackClick : CategoryFormAction
    data class OnNameChange(val value: String) : CategoryFormAction
    data class OnColorSelected(val argb: Int) : CategoryFormAction
    data class OnIconSelected(val key: String) : CategoryFormAction
    data class OnLimitToggle(val enabled: Boolean) : CategoryFormAction
    data class OnLimitAmountChange(val value: String) : CategoryFormAction
    data object OnSaveClick : CategoryFormAction
}
