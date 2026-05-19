package com.software.financetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.software.financetracker.data.local.category.CategoryDao
import com.software.financetracker.data.local.category.CategoryEntity
import com.software.financetracker.data.local.expense.ExpenseDao
import com.software.financetracker.data.local.expense.ExpenseEntity
import com.software.financetracker.data.local.notification.NotificationStateDao
import com.software.financetracker.data.local.notification.NotificationStateEntity

@Database(
    entities = [CategoryEntity::class, ExpenseEntity::class, NotificationStateEntity::class],
    version = 2,
    exportSchema = true
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun notificationStateDao(): NotificationStateDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `notification_states` (
                        `categoryId` INTEGER NOT NULL,
                        `firedAt80Percent` INTEGER NOT NULL DEFAULT 0,
                        `firedAt100Percent` INTEGER NOT NULL DEFAULT 0,
                        `forMonth` TEXT NOT NULL DEFAULT '',
                        PRIMARY KEY(`categoryId`)
                    )"""
                )
            }
        }
    }
}
