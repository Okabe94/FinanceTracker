package com.software.financetracker.data.local.notification

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_states")
data class NotificationStateEntity(
    @PrimaryKey val categoryId: Long,
    val firedAt80Percent: Boolean = false,
    val firedAt100Percent: Boolean = false,
    val forMonth: String = ""
)
