package com.software.financetracker.data.local.investment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates")
data class ExchangeRateEntity(
    @PrimaryKey val fromCurrency: String,
    val toCurrency: String,
    val rate: Double,
    val updatedDate: String
)
