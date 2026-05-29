package com.software.financetracker.data.local.investment

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentDao {
    @Query("SELECT * FROM investments ORDER BY name ASC")
    fun observeAll(): Flow<List<InvestmentEntity>>

    @Query("SELECT * FROM investments WHERE id = :id")
    suspend fun getById(id: Long): InvestmentEntity?

    @Query("SELECT * FROM investments")
    suspend fun getAll(): List<InvestmentEntity>

    @Insert
    suspend fun insert(entity: InvestmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<InvestmentEntity>)

    @Update
    suspend fun update(entity: InvestmentEntity)

    @Delete
    suspend fun delete(entity: InvestmentEntity)

    @Query("DELETE FROM investments")
    suspend fun deleteAll()
}
