package com.software.financetracker.core.backup

import com.software.financetracker.data.local.category.CategoryEntity
import com.software.financetracker.data.local.expense.ExpenseEntity
import com.software.financetracker.data.local.goal.GoalEntity
import com.software.financetracker.data.local.income.IncomeEntity
import com.software.financetracker.data.local.income.RecurringIncomeEntity
import com.software.financetracker.data.local.investment.ExchangeRateEntity
import com.software.financetracker.data.local.investment.InvestmentEntryEntity
import com.software.financetracker.data.local.investment.InvestmentEntity
import com.software.financetracker.data.local.recurring.RecurringExpenseEntity
import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val version: Int = 1,
    val exportedAt: String,
    val preferences: BackupPreferences,
    val categories: List<CategoryEntity>,
    val expenses: List<ExpenseEntity>,
    val recurringExpenses: List<RecurringExpenseEntity>,
    val income: List<IncomeEntity>,
    val recurringIncome: List<RecurringIncomeEntity>,
    val investments: List<InvestmentEntity>,
    val investmentEntries: List<InvestmentEntryEntity>,
    val exchangeRates: List<ExchangeRateEntity>,
    val goals: List<GoalEntity>
)
