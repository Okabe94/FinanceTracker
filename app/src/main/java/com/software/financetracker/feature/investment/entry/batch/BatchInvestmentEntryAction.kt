package com.software.financetracker.feature.investment.entry.batch

import com.software.financetracker.domain.model.investment.EntryType

sealed interface BatchInvestmentEntryAction {
    data object OnAddRow : BatchInvestmentEntryAction
    data class OnRemoveRow(val rowId: Int) : BatchInvestmentEntryAction
    data class OnTypeSelected(val rowId: Int, val type: EntryType) : BatchInvestmentEntryAction
    data class OnAmountChange(val rowId: Int, val value: String) : BatchInvestmentEntryAction
    data class OnDateFieldClick(val rowId: Int) : BatchInvestmentEntryAction
    data class OnDateSelected(val rowId: Int, val epochMillis: Long) : BatchInvestmentEntryAction
    data object OnDismissDatePicker : BatchInvestmentEntryAction
    data class OnNotesChange(val rowId: Int, val value: String) : BatchInvestmentEntryAction
    data object OnSaveAll : BatchInvestmentEntryAction
    data object OnBackClick : BatchInvestmentEntryAction
}
