package com.software.financetracker.data.local.expense

data class TopExpenseRow(
    val id: Long,
    val categoryId: Long,
    val amountCop: Long,
    val description: String,
    val date: String,
    val categoryName: String,
    val colorArgb: Int
)
