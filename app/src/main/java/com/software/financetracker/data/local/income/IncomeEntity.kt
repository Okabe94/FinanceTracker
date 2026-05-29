package com.software.financetracker.data.local.income

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "income", indices = [Index("date")])
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountCop: Long,
    val source: String,
    val date: String,
    val notes: String = "",
    val recurringIncomeId: Long? = null
)
