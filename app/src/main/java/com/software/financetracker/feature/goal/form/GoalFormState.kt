package com.software.financetracker.feature.goal.form

import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.core.util.DateUtil
import java.time.LocalDate

data class GoalFormState(
    val goalId: Long? = null,
    val name: String = "",
    val nameError: Boolean = false,
    val targetAmountInput: String = "",
    val targetAmountError: UiText? = null,
    val selectedDateStorage: String = LocalDate.now().plusMonths(6).toString(),
    val displayDate: String = DateUtil.toDisplayDate(LocalDate.now().plusMonths(6).toString()),
    val showDatePicker: Boolean = false,
    val selectedColorArgb: Int = 0xFF039BE5.toInt(),
    val showDeleteConfirmDialog: Boolean = false,
    val isSaving: Boolean = false
)
