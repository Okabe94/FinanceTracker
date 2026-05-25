package com.software.financetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.software.financetracker.data.local.category.CategoryDao
import com.software.financetracker.data.local.category.CategoryEntity
import com.software.financetracker.data.local.expense.ExpenseDao
import com.software.financetracker.data.local.expense.ExpenseEntity
import com.software.financetracker.data.local.investment.ExchangeRateDao
import com.software.financetracker.data.local.investment.ExchangeRateEntity
import com.software.financetracker.data.local.investment.InvestmentDao
import com.software.financetracker.data.local.investment.InvestmentEntryDao
import com.software.financetracker.data.local.investment.InvestmentEntryEntity
import com.software.financetracker.data.local.investment.InvestmentEntity
import com.software.financetracker.data.local.goal.GoalDao
import com.software.financetracker.data.local.goal.GoalEntity
import com.software.financetracker.data.local.income.IncomeDao
import com.software.financetracker.data.local.income.IncomeEntity
import com.software.financetracker.data.local.income.RecurringIncomeDao
import com.software.financetracker.data.local.income.RecurringIncomeEntity
import com.software.financetracker.data.local.notification.NotificationStateDao
import com.software.financetracker.data.local.notification.NotificationStateEntity
import com.software.financetracker.data.local.recurring.RecurringExpenseDao
import com.software.financetracker.data.local.recurring.RecurringExpenseEntity

@Database(
    entities = [
        CategoryEntity::class,
        ExpenseEntity::class,
        NotificationStateEntity::class,
        RecurringExpenseEntity::class,
        InvestmentEntity::class,
        InvestmentEntryEntity::class,
        ExchangeRateEntity::class,
        IncomeEntity::class,
        GoalEntity::class,
        RecurringIncomeEntity::class
    ],
    version = 9,
    exportSchema = true
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun notificationStateDao(): NotificationStateDao
    abstract fun recurringExpenseDao(): RecurringExpenseDao
    abstract fun investmentDao(): InvestmentDao
    abstract fun investmentEntryDao(): InvestmentEntryDao
    abstract fun exchangeRateDao(): ExchangeRateDao
    abstract fun incomeDao(): IncomeDao
    abstract fun recurringIncomeDao(): RecurringIncomeDao
    abstract fun goalDao(): GoalDao

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

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `investments` (
                        `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        `name` TEXT NOT NULL,
                        `currency` TEXT NOT NULL,
                        `colorArgb` INTEGER NOT NULL,
                        `iconKey` TEXT NOT NULL,
                        `annualRatePercent` REAL,
                        `maturityDate` TEXT,
                        `createdDate` TEXT NOT NULL
                    )"""
                )
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `investment_entries` (
                        `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        `investmentId` INTEGER NOT NULL,
                        `entryType` TEXT NOT NULL,
                        `amountMinorUnits` INTEGER NOT NULL DEFAULT 0,
                        `date` TEXT NOT NULL,
                        `notes` TEXT NOT NULL DEFAULT '',
                        FOREIGN KEY(`investmentId`) REFERENCES `investments`(`id`) ON DELETE CASCADE
                    )"""
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_investment_entries_investmentId` ON `investment_entries`(`investmentId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_investment_entries_date` ON `investment_entries`(`date`)")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `exchange_rates` (
                        `fromCurrency` TEXT NOT NULL PRIMARY KEY,
                        `toCurrency` TEXT NOT NULL,
                        `rate` REAL NOT NULL,
                        `updatedDate` TEXT NOT NULL
                    )"""
                )
                db.execSQL("ALTER TABLE `investments` ADD COLUMN `targetValueMinorUnits` INTEGER")
                db.execSQL("ALTER TABLE `investments` ADD COLUMN `targetDate` TEXT")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `income` (
                        `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        `amountCop` INTEGER NOT NULL,
                        `source` TEXT NOT NULL,
                        `date` TEXT NOT NULL,
                        `notes` TEXT NOT NULL DEFAULT ''
                    )"""
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_income_date` ON `income`(`date`)")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `goals` (
                        `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        `name` TEXT NOT NULL,
                        `targetAmountCop` INTEGER NOT NULL,
                        `currentAmountCop` INTEGER NOT NULL DEFAULT 0,
                        `deadlineDate` TEXT NOT NULL,
                        `colorArgb` INTEGER NOT NULL,
                        `isAchieved` INTEGER NOT NULL DEFAULT 0
                    )"""
                )
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `recurring_income` (
                        `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        `amountCop` INTEGER NOT NULL,
                        `source` TEXT NOT NULL,
                        `notes` TEXT NOT NULL DEFAULT '',
                        `recurrenceType` TEXT NOT NULL,
                        `startDate` TEXT NOT NULL,
                        `nextDueDate` TEXT NOT NULL,
                        `isActive` INTEGER NOT NULL DEFAULT 1
                    )"""
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_recurring_income_nextDueDate` ON `recurring_income`(`nextDueDate`)")
                db.execSQL("ALTER TABLE `income` ADD COLUMN `recurringIncomeId` INTEGER DEFAULT NULL")
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `categories` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
