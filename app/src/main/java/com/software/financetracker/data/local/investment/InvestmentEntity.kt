package com.software.financetracker.data.local.investment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val currency: String,
    val colorArgb: Int,
    val iconKey: String,
    val annualRatePercent: Double? = null,
    val maturityDate: String? = null,
    val createdDate: String,
    val targetValueMinorUnits: Long? = null,
    val targetDate: String? = null
)
