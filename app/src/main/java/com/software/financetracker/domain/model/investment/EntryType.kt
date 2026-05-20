package com.software.financetracker.domain.model.investment

enum class EntryType(val storageKey: String, val labelEs: String) {
    CASH_INJECTION("CASH_INJECTION", "Aporte"),
    VALUE_SNAPSHOT("VALUE_SNAPSHOT", "Valor actual"),
    WITHDRAWAL("WITHDRAWAL", "Retiro"),
    DIVIDEND("DIVIDEND", "Dividendo"),
    NOTE("NOTE", "Nota")
}

fun String.toEntryType(): EntryType =
    EntryType.entries.firstOrNull { it.storageKey == this } ?: EntryType.NOTE
