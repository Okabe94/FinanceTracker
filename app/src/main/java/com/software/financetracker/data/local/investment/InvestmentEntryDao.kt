package com.software.financetracker.data.local.investment

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentEntryDao {
    @Query("SELECT * FROM investment_entries WHERE investmentId = :investmentId ORDER BY date DESC, id DESC")
    fun observeByInvestment(investmentId: Long): Flow<List<InvestmentEntryEntity>>

    @Query("SELECT * FROM investment_entries WHERE investmentId = :investmentId ORDER BY date ASC, id ASC")
    suspend fun getAllByInvestmentAsc(investmentId: Long): List<InvestmentEntryEntity>

    @Query("SELECT * FROM investment_entries WHERE id = :id")
    suspend fun getById(id: Long): InvestmentEntryEntity?

    @Query("SELECT * FROM investment_entries")
    suspend fun getAll(): List<InvestmentEntryEntity>

    @Insert
    suspend fun insert(entity: InvestmentEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<InvestmentEntryEntity>)

    @Update
    suspend fun update(entity: InvestmentEntryEntity)

    @Delete
    suspend fun delete(entity: InvestmentEntryEntity)

    @Query("DELETE FROM investment_entries")
    suspend fun deleteAll()
}
