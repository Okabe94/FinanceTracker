package com.software.financetracker.domain.model

sealed interface RecurrenceType {
    data object Daily : RecurrenceType
    data object Weekly : RecurrenceType
    data object Biweekly : RecurrenceType
    data object Monthly : RecurrenceType
}

fun RecurrenceType.toStorageString(): String = when (this) {
    RecurrenceType.Daily -> "DAILY"
    RecurrenceType.Weekly -> "WEEKLY"
    RecurrenceType.Biweekly -> "BIWEEKLY"
    RecurrenceType.Monthly -> "MONTHLY"
}

fun String.toRecurrenceType(): RecurrenceType = when (this) {
    "DAILY" -> RecurrenceType.Daily
    "WEEKLY" -> RecurrenceType.Weekly
    "BIWEEKLY" -> RecurrenceType.Biweekly
    else -> RecurrenceType.Monthly
}

fun RecurrenceType.displayName(): String = when (this) {
    RecurrenceType.Daily -> "Diario"
    RecurrenceType.Weekly -> "Semanal"
    RecurrenceType.Biweekly -> "Quincenal"
    RecurrenceType.Monthly -> "Mensual"
}
