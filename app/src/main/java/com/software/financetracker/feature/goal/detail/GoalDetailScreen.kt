package com.software.financetracker.feature.goal.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.software.financetracker.core.presentation.CopVisualTransformation
import com.software.financetracker.feature.goal.list.GoalUiModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    state: GoalDetailState,
    onAction: (GoalDetailAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.goal?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = { onAction(GoalDetailAction.OnBackClick) }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { onAction(GoalDetailAction.OnEditClick) }) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Editar")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading || state.goal == null -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            else -> GoalDetailContent(
                goal = state.goal,
                onAction = onAction,
                showContributionDialog = state.showContributionDialog,
                contributionInput = state.contributionInput,
                innerPadding = innerPadding
            )
        }

        if (state.showContributionDialog) {
            AlertDialog(
                onDismissRequest = { onAction(GoalDetailAction.OnContributionDismiss) },
                title = { Text("Agregar aporte") },
                text = {
                    OutlinedTextField(
                        value = state.contributionInput,
                        onValueChange = { onAction(GoalDetailAction.OnContributionChange(it.filter { c -> c.isDigit() })) },
                        label = { Text("Monto (COP)") },
                        prefix = { Text("$ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = CopVisualTransformation(),
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(onClick = { onAction(GoalDetailAction.OnContributionConfirm) }) {
                        Text("Agregar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onAction(GoalDetailAction.OnContributionDismiss) }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun GoalDetailContent(
    goal: GoalUiModel,
    onAction: (GoalDetailAction) -> Unit,
    showContributionDialog: Boolean,
    contributionInput: String,
    innerPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Progreso", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${goal.progressPercent.toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(goal.colorArgb)
                    )
                }
                LinearProgressIndicator(
                    progress = { goal.progressPercent / 100f },
                    color = Color(goal.colorArgb),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth().clip(CircleShape)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(formatCop(goal.currentAmountCop), style = MaterialTheme.typography.bodyMedium)
                    Text(formatCop(goal.targetAmountCop), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Detalles", style = MaterialTheme.typography.titleMedium)
                InfoRow("Falta", formatCop(goal.remainingCop))
                InfoRow("Fecha límite", goal.deadlineDisplay)
                if (goal.requiredMonthlyCop != null) {
                    InfoRow("Ahorro mensual requerido", formatCop(goal.requiredMonthlyCop))
                }
                if (goal.isOverdue) {
                    Text("Esta meta está vencida", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error)
                }
            }
        }

        if (!goal.isOverdue && goal.progressPercent < 100f) {
            Button(
                onClick = { onAction(GoalDetailAction.OnAddContributionClick) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Agregar aporte")
            }

            OutlinedButton(
                onClick = { onAction(GoalDetailAction.OnMarkAchievedClick) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Marcar como lograda")
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

private fun formatCop(amount: Long): String =
    "$ " + NumberFormat.getNumberInstance(Locale("es", "CO")).format(amount)
