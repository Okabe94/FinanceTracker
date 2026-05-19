package com.software.financetracker.feature.category.form

import com.software.financetracker.core.presentation.UiText

data class CategoryFormState(
    val categoryId: Long? = null,
    val name: String = "",
    val nameError: UiText? = null,
    val selectedColorArgb: Int = 0xFFE53935.toInt(),
    val selectedIconKey: String = "shopping_cart",
    val hasLimit: Boolean = false,
    val limitInputCop: String = "",
    val limitError: UiText? = null,
    val isSaving: Boolean = false
)
