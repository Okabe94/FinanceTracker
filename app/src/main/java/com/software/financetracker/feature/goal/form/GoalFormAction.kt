package com.software.financetracker.feature.goal.form

sealed interface GoalFormAction {
    data object OnBackClick : GoalFormAction
    data class OnNameChange(val value: String) : GoalFormAction
    data class OnTargetAmountChange(val value: String) : GoalFormAction
    data object OnDateFieldClick : GoalFormAction
    data class OnDateSelected(val epochMillis: Long) : GoalFormAction
    data object OnDatePickerDismiss : GoalFormAction
    data class OnColorSelected(val colorArgb: Int) : GoalFormAction
    data object OnSaveClick : GoalFormAction
    data object OnDeleteClick : GoalFormAction
    data object OnDeleteConfirm : GoalFormAction
    data object OnDeleteDismiss : GoalFormAction
}
