package com.software.financetracker.feature.investment.entry

import com.software.financetracker.domain.model.investment.EntryType

sealed interface InvestmentEntryFormAction {
    data object OnBackClick : InvestmentEntryFormAction
    data class OnTypeSelected(val type: EntryType) : InvestmentEntryFormAction
    data class OnAmountChange(val value: String) : InvestmentEntryFormAction
    data object OnDateClick : InvestmentEntryFormAction
    data class OnDateSelected(val dateMillis: Long) : InvestmentEntryFormAction
    data object OnDatePickerDismiss : InvestmentEntryFormAction
    data class OnNotesChange(val value: String) : InvestmentEntryFormAction
    data object OnSaveClick : InvestmentEntryFormAction
    data object OnDeleteClick : InvestmentEntryFormAction
    data object OnDeleteConfirm : InvestmentEntryFormAction
    data object OnDeleteDismiss : InvestmentEntryFormAction
}
