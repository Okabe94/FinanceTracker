package com.software.financetracker.feature.category.form

import com.software.financetracker.core.presentation.UiText

sealed interface CategoryFormEvent {
    data object NavigateBack : CategoryFormEvent
    data class ShowError(val message: UiText) : CategoryFormEvent
}
