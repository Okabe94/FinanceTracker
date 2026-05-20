package com.software.financetracker.data.local.investment

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "investment_entries",
    foreignKeys = [ForeignKey(
        entity = InvestmentEntity::class,
        parentColumns = ["id"],
        childColumns = ["investmentId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("investmentId"), Index("date")]
)
data class InvestmentEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val investmentId: Long,
    val entryType: String,
    val amountMinorUnits: Long = 0,
    val date: String,
    val notes: String = ""
)
