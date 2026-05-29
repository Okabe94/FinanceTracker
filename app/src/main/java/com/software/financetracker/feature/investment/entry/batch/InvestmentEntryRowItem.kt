package com.software.financetracker.feature.investment.entry.batch

import com.software.financetracker.core.presentation.UiText
import com.software.financetracker.domain.model.investment.EntryType

data class InvestmentEntryRowItem(
    val rowId: Int,
    val selectedType: EntryType = EntryType.CASH_INJECTION,
    val amountInput: String = "",
    val amountError: UiText? = null,
    val dateStorage: String = "",
    val displayDate: String = "",
    val showDatePicker: Boolean = false,
    val notes: String = ""
) {
    val showAmountField: Boolean get() = selectedType != EntryType.NOTE
}
