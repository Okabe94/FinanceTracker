package com.software.financetracker.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.software.financetracker.MainActivity
import com.software.financetracker.core.util.DateUtil
import com.software.financetracker.data.local.category.CategoryDao
import com.software.financetracker.data.local.expense.ExpenseDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.NumberFormat
import java.time.YearMonth
import java.util.Locale

class BudgetWidget : GlanceAppWidget(), KoinComponent {
    private val categoryDao: CategoryDao by inject()
    private val expenseDao: ExpenseDao by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val currentMonth = DateUtil.currentYearMonth()
        val ym = YearMonth.parse(currentMonth)
        val startDate = ym.atDay(1).toString()
        val endDate = ym.atEndOfMonth().toString()

        val categories = categoryDao.getAll()
        val totals = expenseDao.getMonthlyTotalsByCategory(startDate, endDate)
            .associateBy { it.categoryId }

        val totalSpent = totals.values.sumOf { it.total }
        val totalLimit = categories.mapNotNull { it.monthlyLimitCop }.sum()
        val hasAnyLimit = totalLimit > 0
        val hasExpenses = totalSpent > 0
        val progress = if (hasAnyLimit) (totalSpent.toFloat() / totalLimit).coerceIn(0f, 1f) else 0f
        val progressPercent = (progress * 100).toInt()

        provideContent {
            GlanceTheme {
                WidgetContent(
                    totalSpent = totalSpent,
                    hasAnyLimit = hasAnyLimit,
                    hasExpenses = hasExpenses,
                    progress = progress,
                    progressPercent = progressPercent
                )
            }
        }
    }
}

@Composable
private fun WidgetContent(
    totalSpent: Long,
    hasAnyLimit: Boolean,
    hasExpenses: Boolean,
    progress: Float,
    progressPercent: Int
) {
    val formatter = NumberFormat.getNumberInstance(Locale("es", "CO"))

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.widgetBackground)
            .clickable(actionStartActivity<MainActivity>())
            .padding(12.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            Text(
                text = "Finance Tracker",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            if (!hasExpenses) {
                Text(
                    text = "Sin gastos este mes",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            } else {
                Text(
                    text = "$ ${formatter.format(totalSpent)}",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "gastado este mes",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )

                if (hasAnyLimit) {
                    Spacer(modifier = GlanceModifier.height(10.dp))
                    ProgressBar(progress = progress)
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = "$progressPercent% del límite",
                        style = TextStyle(
                            color = if (progress >= 1f) GlanceTheme.colors.error
                            else GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressBar(progress: Float) {
    val trackWidth = 160.dp
    val fillWidth = (trackWidth.value * progress.coerceIn(0f, 1f)).dp
    val fillColor = if (progress >= 1f) GlanceTheme.colors.error else GlanceTheme.colors.primary

    Box(
        modifier = GlanceModifier
            .width(trackWidth)
            .height(6.dp)
            .background(GlanceTheme.colors.surfaceVariant)
    ) {
        if (fillWidth.value > 0f) {
            Box(
                modifier = GlanceModifier
                    .width(fillWidth)
                    .height(6.dp)
                    .background(fillColor)
            ) {}
        }
    }
}
