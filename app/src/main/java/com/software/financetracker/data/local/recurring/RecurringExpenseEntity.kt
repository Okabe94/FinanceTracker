package com.software.financetracker.data.local.recurring

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.software.financetracker.data.local.category.CategoryEntity

@Entity(
    tableName = "recurring_expenses",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("categoryId"), Index("nextDueDate")]
)
data class RecurringExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val amountCop: Long,
    val description: String,
    val recurrenceType: String,
    val startDate: String,
    val nextDueDate: String,
    val isActive: Boolean = true
)
