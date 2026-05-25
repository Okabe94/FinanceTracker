package com.software.financetracker.feature.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.software.financetracker.R
import com.software.financetracker.feature.goal.list.GoalUiModel
import com.software.financetracker.feature.home.components.CategoryCard
import com.software.financetracker.feature.home.components.IncomeCard
import com.software.financetracker.feature.home.components.SummaryCard
import com.software.financetracker.feature.home.components.formatCop
import com.software.financetracker.ui.components.MonthSelector
import com.software.financetracker.ui.theme.Shapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
) {
    var isFabExpanded by remember { mutableStateOf(false) }
    val fabRotation by animateFloatAsState(
        targetValue = if (isFabExpanded) 45f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "fabRotation"
    )

    Scaffold(
        topBar = {
            MonthSelector(
                displayMonth = state.displayMonth,
                isCurrentMonth = state.isCurrentMonth,
                onPrevious = { onAction(HomeAction.OnPreviousMonthClick) },
                onNext = { onAction(HomeAction.OnNextMonthClick) },
                onGoToCurrentMonth = { onAction(HomeAction.OnGoToCurrentMonthClick) },
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = { onAction(HomeAction.OnMetricsClick) },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(
                        Icons.Rounded.BarChart,
                        contentDescription = stringResource(R.string.home_fab_metrics_cd)
                    )
                }

                AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = expandVertically(
                        animationSpec = tween(durationMillis = 250),
                        expandFrom = Alignment.Bottom
                    ) + fadeIn(animationSpec = tween(durationMillis = 200)),
                    exit = shrinkVertically(
                        animationSpec = tween(durationMillis = 200),
                        shrinkTowards = Alignment.Bottom
                    ) + fadeOut(animationSpec = tween(durationMillis = 150))
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FabMenuItem(label = stringResource(R.string.home_fab_new_category), icon = {
                            Icon(
                                Icons.Rounded.Category,
                                contentDescription = stringResource(R.string.home_fab_new_category)
                            )
                        }) {
                            isFabExpanded = false
                            onAction(HomeAction.OnAddCategoryClick)
                        }
                        FabMenuItem(label = stringResource(R.string.home_fab_new_expense), icon = {
                            Icon(
                                Icons.Rounded.Receipt,
                                contentDescription = stringResource(R.string.home_fab_new_expense)
                            )
                        }) {
                            isFabExpanded = false
                            onAction(HomeAction.OnAddExpenseClick)
                        }
                        FabMenuItem(label = stringResource(R.string.home_fab_new_income), icon = {
                            Icon(
                                Icons.Rounded.AttachMoney,
                                contentDescription = stringResource(R.string.home_fab_new_income)
                            )
                        }) {
                            isFabExpanded = false
                            onAction(HomeAction.OnAddIncomeClick)
                        }
                        FabMenuItem(label = stringResource(R.string.home_fab_new_goal), icon = {
                            Icon(
                                Icons.Rounded.Savings,
                                contentDescription = stringResource(R.string.home_fab_new_goal)
                            )
                        }) {
                            isFabExpanded = false
                            onAction(HomeAction.OnAddGoalClick)
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = if (isFabExpanded) stringResource(R.string.action_close) else stringResource(
                            R.string.fab_add_cd
                        ),
                        modifier = Modifier.graphicsLayer { rotationZ = fabRotation }
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val contentState = when {
            state.isLoading -> 0
            state.categories.isEmpty() -> 1
            else -> 2
        }
        AnimatedContent(
            targetState = contentState,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 300)) togetherWith
                        fadeOut(animationSpec = tween(durationMillis = 150))
            },
            label = "homeContent"
        ) { target ->
            when (target) {
                0 -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                1 -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AccountBalanceWallet,
                            contentDescription = null,
                            modifier = Modifier.padding(bottom = 8.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.home_empty_message),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(
                            onClick = { onAction(HomeAction.OnAddCategoryClick) },
                            shape = Shapes.large
                        ) {
                            Text(stringResource(R.string.home_fab_new_category))
                        }
                    }
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 120.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SummaryCard(
                            totalSpent = state.totalSpent,
                            totalLimit = state.totalLimit,
                            hasAnyLimit = state.hasAnyLimit
                        )
                    }

                    if (state.hasIncomeData) {
                        item {
                            IncomeCard(
                                totalIncomeCop = state.totalIncomeCop,
                                netBalanceCop = state.netBalanceCop,
                                onClick = { onAction(HomeAction.OnIncomeCardClick) }
                            )
                        }
                    }

                    if (state.hasGoals) {
                        item {
                            GoalsSection(
                                goals = state.activeGoals,
                                onGoalClick = { onAction(HomeAction.OnGoalCardClick(it)) },
                                onViewAllClick = { onAction(HomeAction.OnViewAllGoalsClick) }
                            )
                        }
                    }

                    items(state.categories, key = { it.id }) { category ->
                        CategoryCard(
                            category = category,
                            onClick = { onAction(HomeAction.OnCategoryClick(category.id)) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FabMenuItem(
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            tonalElevation = 2.dp
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
        SmallFloatingActionButton(onClick = onClick, content = icon)
    }
}

@Composable
private fun GoalsSection(
    goals: List<GoalUiModel>,
    onGoalClick: (Long) -> Unit,
    onViewAllClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.home_goals_title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        goals.take(3).forEach { goal ->
            GoalCompactCard(goal = goal, onClick = { onGoalClick(goal.id) })
        }
        if (goals.size > 3) {
            TextButton(onClick = onViewAllClick, modifier = Modifier.align(Alignment.End)) {
                Text(stringResource(R.string.home_goals_view_all, goals.size))
            }
        }
    }
}

@Composable
private fun GoalCompactCard(
    goal: GoalUiModel,
    onClick: () -> Unit,
) {
    ElevatedCard(
        onClick = onClick,
        shape = Shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    goal.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${goal.progressPercent.toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            LinearProgressIndicator(
                progress = { (goal.progressPercent / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatCop(goal.currentAmountCop),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(
                        R.string.home_goal_target_amount,
                        formatCop(goal.targetAmountCop)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = stringResource(R.string.home_goal_deadline, goal.deadlineDisplay),
                style = MaterialTheme.typography.bodySmall,
                color = if (goal.isOverdue) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
