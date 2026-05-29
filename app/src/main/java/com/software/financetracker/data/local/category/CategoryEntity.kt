package com.software.financetracker.data.local.category

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorArgb: Int,
    val iconKey: String,
    val monthlyLimitCop: Long?,
    val updatedAt: Long = System.currentTimeMillis()
)
