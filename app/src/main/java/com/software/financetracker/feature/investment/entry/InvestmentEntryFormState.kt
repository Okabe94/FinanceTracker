package com.software.financetracker.feature.investment.entry

import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.domain.model.investment.EntryType

data class InvestmentEntryFormState(
    val entryId: Long? = null,
    val investmentId: Long = 0L,
    val investmentCurrency: String = "COP",
    val selectedType: EntryType = EntryType.CASH_INJECTION,
    val amountInput: String = "",
    val amountError: UiText? = null,
    val showAmountField: Boolean = true,
    val dateStorage: String = "",
    val dateDisplay: String = "",
    val showDatePicker: Boolean = false,
    val notes: String = "",
    val isSaving: Boolean = false,
    val showDeleteDialog: Boolean = false
)
