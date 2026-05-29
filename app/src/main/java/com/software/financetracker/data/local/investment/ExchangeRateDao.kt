package com.software.financetracker.data.local.investment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {
    @Upsert
    suspend fun upsert(entity: ExchangeRateEntity)

    @Query("SELECT * FROM exchange_rates")
    fun getAll(): Flow<List<ExchangeRateEntity>>

    @Query("SELECT * FROM exchange_rates")
    suspend fun getAllSnapshot(): List<ExchangeRateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ExchangeRateEntity>)

    @Query("DELETE FROM exchange_rates")
    suspend fun deleteAll()
}
