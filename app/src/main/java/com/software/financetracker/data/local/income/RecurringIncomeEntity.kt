package com.software.financetracker.data.local.income

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recurring_income",
    indices = [Index("nextDueDate")]
)
data class RecurringIncomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountCop: Long,
    val source: String,
    val notes: String = "",
    val recurrenceType: String,
    val startDate: String,
    val nextDueDate: String,
    val isActive: Boolean = true
)
