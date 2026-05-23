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
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.software.financetracker.feature.home.components.CategoryCard
import com.software.financetracker.feature.home.components.SummaryCard
import com.software.financetracker.ui.components.MonthSelector
import com.software.financetracker.ui.components.iconForKey
import com.software.financetracker.ui.theme.Shapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit
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
                onGoToCurrentMonth = { onAction(HomeAction.OnGoToCurrentMonthClick) }
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
                    Icon(Icons.Rounded.TrendingUp, contentDescription = "Ver métricas")
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
                                    text = "Nueva categoría",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            SmallFloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    onAction(HomeAction.OnAddCategoryClick)
                                }
                            ) {
                                Icon(Icons.Rounded.Category, contentDescription = "Nueva categoría")
                            }
                        }

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
                                    text = "Gastos",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            SmallFloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    onAction(HomeAction.OnRecurringExpensesClick)
                                }
                            ) {
                                Icon(Icons.Rounded.Receipt, contentDescription = "Gastos")
                            }
                        }

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
                                    text = "Ingresos",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            SmallFloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    onAction(HomeAction.OnViewIncomeClick)
                                }
                            ) {
                                Icon(Icons.Rounded.AttachMoney, contentDescription = "Ingresos")
                            }
                        }

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
                                    text = "Metas de ahorro",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            SmallFloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    onAction(HomeAction.OnGoalsClick)
                                }
                            ) {
                                Icon(Icons.Rounded.Savings, contentDescription = "Metas de ahorro")
                            }
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
                        contentDescription = if (isFabExpanded) "Cerrar" else "Agregar",
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
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                1 -> Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
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
                            text = "Crea tu primera categoría",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(
                            onClick = { onAction(HomeAction.OnAddCategoryClick) },
                            shape = Shapes.large
                        ) {
                            Text("Nueva categoría")
                        }
                    }
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SummaryCard(
                            totalSpent = state.totalSpent,
                            totalLimit = state.totalLimit,
                            hasAnyLimit = state.hasAnyLimit,
                            totalIncomeCop = state.totalIncomeCop,
                            netBalanceCop = state.netBalanceCop,
                            hasIncomeData = state.hasIncomeData
                        )
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
