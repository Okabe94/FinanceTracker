package com.software.financetracker.data.local.notification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface NotificationStateDao {
    @Query("SELECT * FROM notification_states WHERE categoryId = :id")
    suspend fun getById(id: Long): NotificationStateEntity?

    @Upsert
    suspend fun upsert(entity: NotificationStateEntity)

    @Query("DELETE FROM notification_states WHERE forMonth != :currentMonth")
    suspend fun clearStaleMonths(currentMonth: String)
}
