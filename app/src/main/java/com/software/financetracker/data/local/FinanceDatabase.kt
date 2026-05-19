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
import com.software.financetracker.data.local.recurring.RecurringExpenseDao
import com.software.financetracker.data.local.recurring.RecurringExpenseEntity

@Database(
    entities = [
        CategoryEntity::class,
        ExpenseEntity::class,
        NotificationStateEntity::class,
        RecurringExpenseEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun notificationStateDao(): NotificationStateDao
    abstract fun recurringExpenseDao(): RecurringExpenseDao

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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `recurring_expenses` (
                        `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        `categoryId` INTEGER NOT NULL,
                        `amountCop` INTEGER NOT NULL,
                        `description` TEXT NOT NULL,
                        `recurrenceType` TEXT NOT NULL,
                        `startDate` TEXT NOT NULL,
                        `nextDueDate` TEXT NOT NULL,
                        `isActive` INTEGER NOT NULL DEFAULT 1,
                        FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON DELETE CASCADE
                    )"""
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_recurring_expenses_categoryId` ON `recurring_expenses`(`categoryId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_recurring_expenses_nextDueDate` ON `recurring_expenses`(`nextDueDate`)")
                db.execSQL("ALTER TABLE `expenses` ADD COLUMN `recurringExpenseId` INTEGER DEFAULT NULL")
            }
        }
    }
}
