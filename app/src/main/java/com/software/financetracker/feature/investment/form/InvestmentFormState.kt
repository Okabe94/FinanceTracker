package com.software.financetracker.feature.investment.form

import com.software.financetracker.core.presentation.UiText

data class InvestmentFormState(
    val investmentId: Long? = null,
    val name: String = "",
    val nameError: UiText? = null,
    val selectedCurrency: String = "COP",
    val showCurrencyDropdown: Boolean = false,
    val selectedColorArgb: Int = 0xFF039BE5.toInt(),
    val selectedIconKey: String = "trending_up",
    val hasFixedRoi: Boolean = false,
    val annualRateInput: String = "",
    val annualRateError: UiText? = null,
    val maturityDateStorage: String? = null,
    val maturityDateDisplay: String? = null,
    val showMaturityDatePicker: Boolean = false,
    val isSaving: Boolean = false,
    val showDeleteDialog: Boolean = false
)
