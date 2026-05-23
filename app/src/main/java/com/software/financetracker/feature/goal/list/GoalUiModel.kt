package com.software.financetracker.feature.goal.list

data class GoalUiModel(
    val id: Long,
    val name: String,
    val targetAmountCop: Long,
    val currentAmountCop: Long,
    val progressPercent: Float,
    val remainingCop: Long,
    val deadlineDisplay: String,
    val requiredMonthlyCop: Long?,
    val isOverdue: Boolean,
    val colorArgb: Int
)
