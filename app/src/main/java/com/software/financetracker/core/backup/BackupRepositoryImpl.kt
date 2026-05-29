package com.software.financetracker.core.backup

import com.software.financetracker.core.preferences.UserPreferences
import com.software.financetracker.data.local.category.CategoryDao
import com.software.financetracker.data.local.expense.ExpenseDao
import com.software.financetracker.data.local.goal.GoalDao
import com.software.financetracker.data.local.income.IncomeDao
import com.software.financetracker.data.local.income.RecurringIncomeDao
import com.software.financetracker.data.local.investment.ExchangeRateDao
import com.software.financetracker.data.local.investment.InvestmentDao
import com.software.financetracker.data.local.investment.InvestmentEntryDao
import com.software.financetracker.data.local.recurring.RecurringExpenseDao
import com.software.financetracker.ui.theme.ThemeMode
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BackupRepositoryImpl(
    private val runInTransaction: suspend (suspend () -> Unit) -> Unit,
    private val categoryDao: CategoryDao,
    private val expenseDao: ExpenseDao,
    private val recurringExpenseDao: RecurringExpenseDao,
    private val incomeDao: IncomeDao,
    private val recurringIncomeDao: RecurringIncomeDao,
    private val investmentDao: InvestmentDao,
    private val investmentEntryDao: InvestmentEntryDao,
    private val exchangeRateDao: ExchangeRateDao,
    private val goalDao: GoalDao,
    private val prefs: UserPreferences
) : BackupRepository {

    override suspend fun export(): BackupData {
        val preferences = BackupPreferences(
            notificationsEnabled = prefs.notificationsEnabled.first(),
            themeMode = prefs.themeMode.first().name,
            defaultCurrency = prefs.defaultCurrency.first(),
            useCustomExchangeRates = prefs.useCustomExchangeRates.first(),
            customUsdRate = prefs.customUsdRate.first(),
            customEurRate = prefs.customEurRate.first(),
            customGbpRate = prefs.customGbpRate.first(),
            investmentSortField = prefs.investmentSortField.first(),
            investmentSortDirection = prefs.investmentSortDirection.first(),
            homeSortField = prefs.homeSortField.first(),
            homeSortDirection = prefs.homeSortDirection.first()
        )
        return BackupData(
            exportedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            preferences = preferences,
            categories = categoryDao.getAll(),
            expenses = expenseDao.getAll(),
            recurringExpenses = recurringExpenseDao.getAll(),
            income = incomeDao.getAll(),
            recurringIncome = recurringIncomeDao.getAll(),
            investments = investmentDao.getAll(),
            investmentEntries = investmentEntryDao.getAll(),
            exchangeRates = exchangeRateDao.getAllSnapshot(),
            goals = goalDao.getAll()
        )
    }

    override suspend fun import(data: BackupData) {
        runInTransaction {
            // Delete in child-first order to respect foreign keys
            expenseDao.deleteAll()
            recurringExpenseDao.deleteAll()
            investmentEntryDao.deleteAll()
            incomeDao.deleteAll()
            recurringIncomeDao.deleteAll()
            exchangeRateDao.deleteAll()
            goalDao.deleteAll()
            investmentDao.deleteAll()
            categoryDao.deleteAll()

            // Insert in parent-first order
            categoryDao.insertAll(data.categories)
            investmentDao.insertAll(data.investments)
            expenseDao.insertAll(data.expenses)
            recurringExpenseDao.insertAll(data.recurringExpenses)
            investmentEntryDao.insertAll(data.investmentEntries)
            incomeDao.insertAll(data.income)
            recurringIncomeDao.insertAll(data.recurringIncome)
            exchangeRateDao.insertAll(data.exchangeRates)
            goalDao.insertAll(data.goals)
        }

        val p = data.preferences
        prefs.setNotificationsEnabled(p.notificationsEnabled)
        prefs.setThemeMode(runCatching { ThemeMode.valueOf(p.themeMode) }.getOrDefault(ThemeMode.DARK))
        prefs.setDefaultCurrency(p.defaultCurrency)
        prefs.setUseCustomExchangeRates(p.useCustomExchangeRates)
        prefs.setCustomRates(p.customUsdRate, p.customEurRate, p.customGbpRate)
        prefs.setInvestmentSort(p.investmentSortField, p.investmentSortDirection)
        prefs.setHomeSort(p.homeSortField, p.homeSortDirection)
    }
}
