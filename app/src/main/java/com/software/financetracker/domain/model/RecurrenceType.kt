package com.software.financetracker.domain.model

sealed interface RecurrenceType {
    data object Daily : RecurrenceType
    data object Weekly : RecurrenceType
    data object Biweekly : RecurrenceType
    data object Monthly : RecurrenceType
    data class Custom(val intervalDays: Int) : RecurrenceType
}

fun RecurrenceType.toStorageString(): String = when (this) {
    RecurrenceType.Daily -> "DAILY"
    RecurrenceType.Weekly -> "WEEKLY"
    RecurrenceType.Biweekly -> "BIWEEKLY"
    RecurrenceType.Monthly -> "MONTHLY"
    is RecurrenceType.Custom -> "CUSTOM:$intervalDays"
}

fun String.toRecurrenceType(): RecurrenceType = when {
    this == "DAILY" -> RecurrenceType.Daily
    this == "WEEKLY" -> RecurrenceType.Weekly
    this == "BIWEEKLY" -> RecurrenceType.Biweekly
    startsWith("CUSTOM:") -> RecurrenceType.Custom(substringAfter("CUSTOM:").toIntOrNull() ?: 7)
    else -> RecurrenceType.Monthly
}

fun RecurrenceType.displayName(): String = when (this) {
    RecurrenceType.Daily -> "Diario"
    RecurrenceType.Weekly -> "Semanal"
    RecurrenceType.Biweekly -> "Quincenal"
    RecurrenceType.Monthly -> "Mensual"
    is RecurrenceType.Custom -> "Cada $intervalDays días"
}
