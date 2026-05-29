package com.software.financetracker.data.local.goal

import androidx.room.*
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals WHERE isAchieved = 0 ORDER BY deadlineDate ASC")
    fun observeActive(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE isAchieved = 1 ORDER BY deadlineDate DESC")
    fun observeAchieved(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getById(id: Long): GoalEntity?

    @Query("SELECT * FROM goals")
    suspend fun getAll(): List<GoalEntity>

    @Insert
    suspend fun insert(entity: GoalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<GoalEntity>)

    @Update
    suspend fun update(entity: GoalEntity)

    @Delete
    suspend fun delete(entity: GoalEntity)

    @Query("DELETE FROM goals")
    suspend fun deleteAll()
}
