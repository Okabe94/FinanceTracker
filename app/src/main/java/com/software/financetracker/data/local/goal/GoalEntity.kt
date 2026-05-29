package com.software.financetracker.data.local.goal

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetAmountCop: Long,
    val currentAmountCop: Long = 0L,
    val deadlineDate: String,
    val colorArgb: Int,
    val isAchieved: Boolean = false
)
