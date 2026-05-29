package com.software.financetracker.core.backup

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import com.software.financetracker.data.local.category.CategoryDao
import com.software.financetracker.data.local.category.CategoryEntity
import com.software.financetracker.data.local.expense.CategoryMonthTotal
import com.software.financetracker.data.local.expense.CategoryMonthlyBreakdown
import com.software.financetracker.data.local.expense.DayOfWeekTotal
import com.software.financetracker.data.local.expense.ExpenseDao
import com.software.financetracker.data.local.expense.ExpenseEntity
import com.software.financetracker.data.local.expense.MonthlyTotal
import com.software.financetracker.data.local.expense.TopExpenseRow
import com.software.financetracker.data.local.goal.GoalDao
import com.software.financetracker.data.local.goal.GoalEntity
import com.software.financetracker.data.local.income.IncomeDao
import com.software.financetracker.data.local.income.IncomeEntity
import com.software.financetracker.data.local.income.IncomeMonthlyTotal
import com.software.financetracker.data.local.income.RecurringIncomeDao
import com.software.financetracker.data.local.income.RecurringIncomeEntity
import com.software.financetracker.data.local.investment.ExchangeRateDao
import com.software.financetracker.data.local.investment.ExchangeRateEntity
import com.software.financetracker.data.local.investment.InvestmentDao
import com.software.financetracker.data.local.investment.InvestmentEntryDao
import com.software.financetracker.data.local.investment.InvestmentEntryEntity
import com.software.financetracker.data.local.investment.InvestmentEntity
import com.software.financetracker.data.local.recurring.RecurringExpenseDao
import com.software.financetracker.data.local.recurring.RecurringExpenseEntity
import com.software.financetracker.core.preferences.UserPreferences
import com.software.financetracker.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class BackupRepositoryImplTest {

    private lateinit var categoryDao: TrackingCategoryDao
    private lateinit var expenseDao: TrackingExpenseDao
    private lateinit var recurringExpenseDao: TrackingRecurringExpenseDao
    private lateinit var incomeDao: TrackingIncomeDao
    private lateinit var recurringIncomeDao: TrackingRecurringIncomeDao
    private lateinit var investmentDao: TrackingInvestmentDao
    private lateinit var investmentEntryDao: TrackingInvestmentEntryDao
    private lateinit var exchangeRateDao: TrackingExchangeRateDao
    private lateinit var goalDao: TrackingGoalDao
    private lateinit var prefs: TrackingUserPreferences
    private lateinit var repository: BackupRepositoryImpl

    @Before
    fun setUp() {
        categoryDao = TrackingCategoryDao()
        expenseDao = TrackingExpenseDao()
        recurringExpenseDao = TrackingRecurringExpenseDao()
        incomeDao = TrackingIncomeDao()
        recurringIncomeDao = TrackingRecurringIncomeDao()
        investmentDao = TrackingInvestmentDao()
        investmentEntryDao = TrackingInvestmentEntryDao()
        exchangeRateDao = TrackingExchangeRateDao()
        goalDao = TrackingGoalDao()
        prefs = TrackingUserPreferences()

        repository = BackupRepositoryImpl(
            runInTransaction = { block -> block() },
            categoryDao = categoryDao,
            expenseDao = expenseDao,
            recurringExpenseDao = recurringExpenseDao,
            incomeDao = incomeDao,
            recurringIncomeDao = recurringIncomeDao,
            investmentDao = investmentDao,
            investmentEntryDao = investmentEntryDao,
            exchangeRateDao = exchangeRateDao,
            goalDao = goalDao,
            prefs = prefs
        )
    }

    // region Export

    @Test
    fun `export_includesAllTableData`() = runTest {
        val category = CategoryEntity(id = 1, name = "Alimentación", colorArgb = 0xFF0000.toInt(), iconKey = "food", monthlyLimitCop = null)
        val expense = ExpenseEntity(id = 1, categoryId = 1, amountCop = 50000, description = "Almuerzo", date = "2026-05-28")
        categoryDao.seedData(listOf(category))
        expenseDao.seedData(listOf(expense))

        val backup = repository.export()

        assertThat(backup.categories).containsExactly(category)
        assertThat(backup.expenses).containsExactly(expense)
        assertThat(backup.exportedAt).isNotEmpty()
    }

    @Test
    fun `export_includesPreferences`() = runTest {
        val backup = repository.export()

        assertThat(backup.preferences.defaultCurrency).isEqualTo("COP")
        assertThat(backup.preferences.themeMode).isEqualTo("DARK")
        assertThat(backup.preferences.notificationsEnabled).isEqualTo(true)
    }

    // endregion

    // region Import

    @Test
    fun `import_insertsAllDataFromBackup`() = runTest {
        val category = CategoryEntity(id = 5, name = "Transporte", colorArgb = 0x00FF00.toInt(), iconKey = "bus", monthlyLimitCop = 300000)
        val backup = buildSampleBackup(categories = listOf(category))

        repository.import(backup)

        assertThat(categoryDao.insertedAll!!).containsExactly(category)
    }

    @Test
    fun `import_deletesAllTablesBeforeInsert`() = runTest {
        val backup = buildSampleBackup()

        repository.import(backup)

        assertThat(categoryDao.deleteAllCalled).isTrue()
        assertThat(expenseDao.deleteAllCalled).isTrue()
        assertThat(recurringExpenseDao.deleteAllCalled).isTrue()
        assertThat(incomeDao.deleteAllCalled).isTrue()
        assertThat(recurringIncomeDao.deleteAllCalled).isTrue()
        assertThat(investmentDao.deleteAllCalled).isTrue()
        assertThat(investmentEntryDao.deleteAllCalled).isTrue()
        assertThat(exchangeRateDao.deleteAllCalled).isTrue()
        assertThat(goalDao.deleteAllCalled).isTrue()
    }

    @Test
    fun `import_appliesPreferencesToDataStore`() = runTest {
        val backup = buildSampleBackup()

        repository.import(backup)

        assertThat(prefs.setDefaultCurrencyCalls).containsExactly("USD")
    }

    // endregion

    // region Helpers

    private fun buildSampleBackup(
        categories: List<CategoryEntity> = emptyList()
    ) = BackupData(
        exportedAt = "2026-05-28 10:00",
        preferences = BackupPreferences(
            notificationsEnabled = false,
            themeMode = "LIGHT",
            defaultCurrency = "USD",
            useCustomExchangeRates = false,
            customUsdRate = 0f,
            customEurRate = 0f,
            customGbpRate = 0f,
            investmentSortField = "ALPHABETICAL",
            investmentSortDirection = "ASC",
            homeSortField = "ALPHABETICAL",
            homeSortDirection = "ASC"
        ),
        categories = categories,
        expenses = emptyList(),
        recurringExpenses = emptyList(),
        income = emptyList(),
        recurringIncome = emptyList(),
        investments = emptyList(),
        investmentEntries = emptyList(),
        exchangeRates = emptyList(),
        goals = emptyList()
    )

    // endregion
}

// ---- Tracking DAO stubs ----

private class TrackingCategoryDao : CategoryDao {
    var deleteAllCalled = false
    var insertedAll: List<CategoryEntity>? = null
    private var data: List<CategoryEntity> = emptyList()

    fun seedData(entities: List<CategoryEntity>) { data = entities }

    override fun observeAll(): Flow<List<CategoryEntity>> = emptyFlow()
    override suspend fun getAll(): List<CategoryEntity> = data
    override suspend fun getById(id: Long): CategoryEntity? = null
    override fun observeById(id: Long): Flow<CategoryEntity?> = emptyFlow()
    override suspend fun insert(entity: CategoryEntity): Long = 0L
    override suspend fun insertAll(entities: List<CategoryEntity>) { insertedAll = entities }
    override suspend fun update(entity: CategoryEntity) {}
    override suspend fun delete(entity: CategoryEntity) {}
    override suspend fun deleteAll() { deleteAllCalled = true }
}

private class TrackingExpenseDao : ExpenseDao {
    var deleteAllCalled = false
    private var data: List<ExpenseEntity> = emptyList()

    fun seedData(entities: List<ExpenseEntity>) { data = entities }

    override fun observeByCategory(categoryId: Long): Flow<List<ExpenseEntity>> = emptyFlow()
    override fun observeMonthlyTotalsByCategory(startDate: String, endDate: String): Flow<List<CategoryMonthTotal>> = emptyFlow()
    override suspend fun getMonthlyTotalsByCategory(startDate: String, endDate: String): List<CategoryMonthTotal> = emptyList()
    override fun observeMonthlyTotals(startDate: String, endDate: String): Flow<List<MonthlyTotal>> = emptyFlow()
    override fun observeCategoryMonthlyBreakdown(startDate: String, endDate: String): Flow<List<CategoryMonthlyBreakdown>> = emptyFlow()
    override fun observeTopExpenses(startDate: String, endDate: String, limit: Int): Flow<List<TopExpenseRow>> = emptyFlow()
    override fun observeSpendByDayOfWeek(startDate: String, endDate: String): Flow<List<DayOfWeekTotal>> = emptyFlow()
    override suspend fun getAllInRange(startDate: String, endDate: String): List<TopExpenseRow> = emptyList()
    override suspend fun getById(id: Long): ExpenseEntity? = null
    override suspend fun getAll(): List<ExpenseEntity> = data
    override suspend fun insert(entity: ExpenseEntity): Long = 0L
    override suspend fun insertAll(entities: List<ExpenseEntity>) {}
    override suspend fun update(entity: ExpenseEntity) {}
    override suspend fun delete(entity: ExpenseEntity) {}
    override suspend fun deleteAll() { deleteAllCalled = true }
}

private class TrackingRecurringExpenseDao : RecurringExpenseDao {
    var deleteAllCalled = false
    override fun observeActive(): Flow<List<RecurringExpenseEntity>> = emptyFlow()
    override suspend fun getDueToday(today: String): List<RecurringExpenseEntity> = emptyList()
    override fun observeByCategory(categoryId: Long): Flow<List<RecurringExpenseEntity>> = emptyFlow()
    override suspend fun getById(id: Long): RecurringExpenseEntity? = null
    override suspend fun getAll(): List<RecurringExpenseEntity> = emptyList()
    override suspend fun insert(entity: RecurringExpenseEntity): Long = 0L
    override suspend fun insertAll(entities: List<RecurringExpenseEntity>) {}
    override suspend fun update(entity: RecurringExpenseEntity) {}
    override suspend fun delete(entity: RecurringExpenseEntity) {}
    override suspend fun deleteAll() { deleteAllCalled = true }
}

private class TrackingIncomeDao : IncomeDao {
    var deleteAllCalled = false
    override fun observeInRange(startDate: String, endDate: String): Flow<List<IncomeEntity>> = emptyFlow()
    override fun observeMonthlyTotals(startDate: String, endDate: String): Flow<List<IncomeMonthlyTotal>> = emptyFlow()
    override fun observeTotalInRange(startDate: String, endDate: String): Flow<Long?> = emptyFlow()
    override suspend fun insert(entity: IncomeEntity): Long = 0L
    override suspend fun update(entity: IncomeEntity) {}
    override suspend fun delete(entity: IncomeEntity) {}
    override suspend fun getById(id: Long): IncomeEntity? = null
    override suspend fun getAll(): List<IncomeEntity> = emptyList()
    override suspend fun insertAll(entities: List<IncomeEntity>) {}
    override suspend fun deleteAll() { deleteAllCalled = true }
}

private class TrackingRecurringIncomeDao : RecurringIncomeDao {
    var deleteAllCalled = false
    override fun observeActive(): Flow<List<RecurringIncomeEntity>> = emptyFlow()
    override suspend fun getDueToday(today: String): List<RecurringIncomeEntity> = emptyList()
    override suspend fun getById(id: Long): RecurringIncomeEntity? = null
    override suspend fun getAll(): List<RecurringIncomeEntity> = emptyList()
    override suspend fun insert(entity: RecurringIncomeEntity): Long = 0L
    override suspend fun insertAll(entities: List<RecurringIncomeEntity>) {}
    override suspend fun update(entity: RecurringIncomeEntity) {}
    override suspend fun delete(entity: RecurringIncomeEntity) {}
    override suspend fun deleteAll() { deleteAllCalled = true }
}

private class TrackingInvestmentDao : InvestmentDao {
    var deleteAllCalled = false
    override fun observeAll(): Flow<List<InvestmentEntity>> = emptyFlow()
    override suspend fun getById(id: Long): InvestmentEntity? = null
    override suspend fun getAll(): List<InvestmentEntity> = emptyList()
    override suspend fun insert(entity: InvestmentEntity): Long = 0L
    override suspend fun insertAll(entities: List<InvestmentEntity>) {}
    override suspend fun update(entity: InvestmentEntity) {}
    override suspend fun delete(entity: InvestmentEntity) {}
    override suspend fun deleteAll() { deleteAllCalled = true }
}

private class TrackingInvestmentEntryDao : InvestmentEntryDao {
    var deleteAllCalled = false
    override fun observeByInvestment(investmentId: Long): Flow<List<InvestmentEntryEntity>> = emptyFlow()
    override suspend fun getAllByInvestmentAsc(investmentId: Long): List<InvestmentEntryEntity> = emptyList()
    override suspend fun getById(id: Long): InvestmentEntryEntity? = null
    override suspend fun getAll(): List<InvestmentEntryEntity> = emptyList()
    override suspend fun insert(entity: InvestmentEntryEntity): Long = 0L
    override suspend fun insertAll(entities: List<InvestmentEntryEntity>) {}
    override suspend fun update(entity: InvestmentEntryEntity) {}
    override suspend fun delete(entity: InvestmentEntryEntity) {}
    override suspend fun deleteAll() { deleteAllCalled = true }
}

private class TrackingExchangeRateDao : ExchangeRateDao {
    var deleteAllCalled = false
    override suspend fun upsert(entity: ExchangeRateEntity) {}
    override fun getAll(): Flow<List<ExchangeRateEntity>> = emptyFlow()
    override suspend fun getAllSnapshot(): List<ExchangeRateEntity> = emptyList()
    override suspend fun insertAll(entities: List<ExchangeRateEntity>) {}
    override suspend fun deleteAll() { deleteAllCalled = true }
}

private class TrackingGoalDao : GoalDao {
    var deleteAllCalled = false
    override fun observeActive(): Flow<List<GoalEntity>> = emptyFlow()
    override fun observeAchieved(): Flow<List<GoalEntity>> = emptyFlow()
    override suspend fun getById(id: Long): GoalEntity? = null
    override suspend fun getAll(): List<GoalEntity> = emptyList()
    override suspend fun insert(entity: GoalEntity): Long = 0L
    override suspend fun insertAll(entities: List<GoalEntity>) {}
    override suspend fun update(entity: GoalEntity) {}
    override suspend fun delete(entity: GoalEntity) {}
    override suspend fun deleteAll() { deleteAllCalled = true }
}

private class TrackingUserPreferences : UserPreferences {
    val setDefaultCurrencyCalls = mutableListOf<String>()

    override val notificationsEnabled: kotlinx.coroutines.flow.Flow<Boolean> = kotlinx.coroutines.flow.MutableStateFlow(true)
    override val themeMode: kotlinx.coroutines.flow.Flow<ThemeMode> = kotlinx.coroutines.flow.MutableStateFlow(ThemeMode.DARK)
    override val defaultCurrency: kotlinx.coroutines.flow.Flow<String> = kotlinx.coroutines.flow.MutableStateFlow("COP")
    override val useCustomExchangeRates: kotlinx.coroutines.flow.Flow<Boolean> = kotlinx.coroutines.flow.MutableStateFlow(false)
    override val customUsdRate: kotlinx.coroutines.flow.Flow<Float> = kotlinx.coroutines.flow.MutableStateFlow(0f)
    override val customEurRate: kotlinx.coroutines.flow.Flow<Float> = kotlinx.coroutines.flow.MutableStateFlow(0f)
    override val customGbpRate: kotlinx.coroutines.flow.Flow<Float> = kotlinx.coroutines.flow.MutableStateFlow(0f)
    override val investmentSortField: kotlinx.coroutines.flow.Flow<String> = kotlinx.coroutines.flow.MutableStateFlow("ALPHABETICAL")
    override val investmentSortDirection: kotlinx.coroutines.flow.Flow<String> = kotlinx.coroutines.flow.MutableStateFlow("ASC")
    override val homeSortField: kotlinx.coroutines.flow.Flow<String> = kotlinx.coroutines.flow.MutableStateFlow("ALPHABETICAL")
    override val homeSortDirection: kotlinx.coroutines.flow.Flow<String> = kotlinx.coroutines.flow.MutableStateFlow("ASC")

    override suspend fun setNotificationsEnabled(enabled: Boolean) {}
    override suspend fun setThemeMode(mode: ThemeMode) {}
    override suspend fun setDefaultCurrency(currency: String) { setDefaultCurrencyCalls.add(currency) }
    override suspend fun setUseCustomExchangeRates(enabled: Boolean) {}
    override suspend fun setCustomRates(usd: Float, eur: Float, gbp: Float) {}
    override suspend fun setInvestmentSort(field: String, direction: String) {}
    override suspend fun setHomeSort(field: String, direction: String) {}
}
