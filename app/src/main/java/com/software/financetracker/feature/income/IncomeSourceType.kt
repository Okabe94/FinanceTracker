package com.software.financetracker.feature.income

enum class IncomeSourceType(val displayName: String) {
    SALARY("Salario"),
    PASSIVE("Ingresos pasivos"),
    FREELANCE("Freelance"),
    TRANSFER("Transferencia"),
    OTHER("Otro")
}

fun String.toIncomeSourceType(): IncomeSourceType =
    IncomeSourceType.entries.firstOrNull { it.displayName == this } ?: IncomeSourceType.OTHER
