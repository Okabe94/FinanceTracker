package com.software.financetracker.feature.category.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.software.financetracker.R
import com.software.financetracker.domain.model.displayName
import com.software.financetracker.ui.components.FinanceTrackerFab
import com.software.financetracker.ui.components.MonthSelector
import com.software.financetracker.ui.theme.Shapes
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    state: CategoryDetailState,
    onAction: (CategoryDetailAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.categoryName) },
                navigationIcon = {
                    IconButton(onClick = { onAction(CategoryDetailAction.OnBackClick) }) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onAction(CategoryDetailAction.OnEditCategoryClick) }) {
                        Icon(
                            Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.category_detail_edit_cd)
                        )
                    }
                    IconButton(onClick = { onAction(CategoryDetailAction.OnDeleteCategoryClick) }) {
                        Icon(
                            Icons.Rounded.Delete,
                            contentDescription = stringResource(R.string.category_detail_delete_cd)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FinanceTrackerFab(onClick = { onAction(CategoryDetailAction.OnAddExpenseClick) })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (state.showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { onAction(CategoryDetailAction.OnDeleteDismiss) },
                title = { Text(stringResource(R.string.category_detail_delete_title)) },
                text = {
                    Text(
                        stringResource(
                            R.string.category_detail_delete_message,
                            state.categoryName
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = { onAction(CategoryDetailAction.OnDeleteConfirm) }) {
                        Text(
                            stringResource(R.string.action_delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onAction(CategoryDetailAction.OnDeleteDismiss) }) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MonthSelector(
                displayMonth = state.displayMonth,
                isCurrentMonth = state.isCurrentMonth,
                onPrevious = { onAction(CategoryDetailAction.OnPreviousMonthClick) },
                onNext = { onAction(CategoryDetailAction.OnNextMonthClick) },
                onGoToCurrentMonth = { onAction(CategoryDetailAction.OnGoToCurrentMonthClick) }
            )
            Surface(
                color = Color(0xFF1E3932),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = formatCop(state.amountSpent),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    if (state.monthlyLimitCop != null) {
                        Text(
                            text = stringResource(
                                R.string.category_detail_of_amount,
                                formatCop(state.monthlyLimitCop)
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        val progress = (state.amountSpent.toFloat() / state.monthlyLimitCop)
                            .coerceIn(0f, 1f)
                        val progressColor by animateColorAsState(
                            targetValue = if (state.isOverLimit) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary,
                            animationSpec = tween(durationMillis = 500),
                            label = "progressColor"
                        )
                        LinearProgressIndicator(
                            progress = { progress },
                            color = progressColor,
                            trackColor = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CircleShape)
                        )
                        if (state.isOverLimit) {
                            Text(
                                text = stringResource(R.string.label_over_limit),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            val contentState = when {
                state.isLoading -> 0
                state.recurringExpenses.isEmpty() && state.expenses.isEmpty() -> 1
                else -> 2
            }

            AnimatedContent(
                targetState = contentState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(durationMillis = 300)) togetherWith
                            fadeOut(animationSpec = tween(durationMillis = 150))
                },
                label = "categoryDetailContent"
            ) { target ->
                when (target) {
                    0 -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                        androidx.compose.material3.CircularProgressIndicator()
                    }

                    1 -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(
                            text = stringResource(R.string.category_detail_no_expenses),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (state.recurringExpenses.isNotEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.label_recurring),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            items(state.recurringExpenses, key = { "r${it.id}" }) { template ->
                                RecurringTemplateListItem(
                                    template = template,
                                    onClick = {
                                        onAction(
                                            CategoryDetailAction.OnRecurringExpenseClick(
                                                template.id
                                            )
                                        )
                                    },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                        if (state.expenses.isNotEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.category_detail_section_this_month),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(
                                        top = if (state.recurringExpenses.isNotEmpty()) 8.dp else 0.dp,
                                        bottom = 4.dp
                                    )
                                )
                            }
                            items(state.expenses, key = { it.id }) { expense ->
                                ExpenseListItem(
                                    expense = expense,
                                    onClick = { onAction(CategoryDetailAction.OnExpenseClick(expense.id)) },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecurringTemplateListItem(
    template: RecurringTemplateUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = onClick,
        shape = Shapes.medium,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Rounded.Repeat,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        template.recurrenceType.displayName(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (!template.isActive) {
                        Text(
                            stringResource(R.string.label_paused),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (template.description.isNotBlank()) {
                    Text(template.description, style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                    text = stringResource(R.string.label_next_due, template.displayNextDueDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatCop(template.amountCop),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ExpenseListItem(
    expense: ExpenseUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = onClick,
        shape = Shapes.medium,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (expense.isAutoGenerated) {
                        Icon(
                            Icons.Rounded.Repeat,
                            contentDescription = stringResource(R.string.category_detail_auto_generated_cd),
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (expense.description.isNotBlank()) {
                        Text(expense.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Text(
                    text = expense.displayDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatCop(expense.amountCop),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatCop(amount: Long): String =
    "$ " + NumberFormat.getNumberInstance(Locale("es", "CO")).format(amount)
