package com.software.financetracker.feature.goal.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.software.financetracker.ui.theme.Shapes
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalListScreen(
    state: GoalListState,
    onAction: (GoalListAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Metas de ahorro") },
                navigationIcon = {
                    IconButton(onClick = { onAction(GoalListAction.OnBackClick) }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(GoalListAction.OnAddClick) }) {
                Icon(Icons.Rounded.Add, contentDescription = "Nueva meta")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.activeGoals.isEmpty() && state.achievedGoals.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Savings,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text("Sin metas de ahorro", style = MaterialTheme.typography.titleMedium)
                    Text("Toca + para crear una meta", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.activeGoals.isNotEmpty()) {
                    item { Text("Activas", style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp)) }
                    items(state.activeGoals, key = { it.id }) { goal ->
                        GoalItem(goal = goal, onClick = { onAction(GoalListAction.OnGoalClick(goal.id)) },
                            modifier = Modifier.animateItem())
                    }
                }
                if (state.achievedGoals.isNotEmpty()) {
                    item { Text("Logradas", style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) }
                    items(state.achievedGoals, key = { "achieved_${it.id}" }) { goal ->
                        GoalItem(goal = goal, onClick = { onAction(GoalListAction.OnGoalClick(goal.id)) },
                            modifier = Modifier.animateItem())
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalItem(goal: GoalUiModel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(onClick = onClick, shape = Shapes.medium, modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(goal.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text(formatCop(goal.currentAmountCop) + " / " + formatCop(goal.targetAmountCop),
                    style = MaterialTheme.typography.bodySmall)
            }
            LinearProgressIndicator(
                progress = { goal.progressPercent / 100f },
                color = Color(goal.colorArgb),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth().clip(androidx.compose.foundation.shape.CircleShape)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val statusText = when {
                    goal.isOverdue -> "Vencida"
                    goal.requiredMonthlyCop != null -> "Necesitas ${formatCop(goal.requiredMonthlyCop)}/mes"
                    else -> "Meta lograda"
                }
                Text(statusText, style = MaterialTheme.typography.bodySmall,
                    color = if (goal.isOverdue) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Plazo: ${goal.deadlineDisplay}", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun formatCop(amount: Long): String =
    "$ " + NumberFormat.getNumberInstance(Locale("es", "CO")).format(amount)
