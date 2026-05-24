package com.software.financetracker.feature.income

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.vector.ImageVector

enum class IncomeSourceType(
    val displayName: String,
    val icon: ImageVector,
    val colorArgb: Long
) {
    SALARY("Salario", Icons.Rounded.Work, 0xFF43A047),
    PASSIVE("Ingresos pasivos", Icons.Rounded.TrendingUp, 0xFF1E88E5),
    FREELANCE("Freelance", Icons.Rounded.Code, 0xFF8E24AA),
    TRANSFER("Transferencia", Icons.Rounded.AccountBalance, 0xFFE65100),
    OTHER("Otro", Icons.Rounded.MoreHoriz, 0xFF546E7A)
}

fun String.toIncomeSourceType(): IncomeSourceType =
    IncomeSourceType.entries.firstOrNull { it.displayName == this } ?: IncomeSourceType.OTHER
