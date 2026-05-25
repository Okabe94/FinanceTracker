package com.software.financetracker.feature.goal.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.software.financetracker.R
import com.software.financetracker.core.presentation.CopVisualTransformation
import com.software.financetracker.feature.goal.list.GoalUiModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    state: GoalDetailState,
    onAction: (GoalDetailAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.goal?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = { onAction(GoalDetailAction.OnBackClick) }) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onAction(GoalDetailAction.OnEditClick) }) {
                        Icon(
                            Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.cd_edit)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading || state.goal == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
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
                title = { Text(stringResource(R.string.goal_detail_contribution_dialog_title)) },
                text = {
                    OutlinedTextField(
                        value = state.contributionInput,
                        onValueChange = { onAction(GoalDetailAction.OnContributionChange(it.filter { c -> c.isDigit() })) },
                        label = { Text(stringResource(R.string.label_amount_cop)) },
                        prefix = { Text("$ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = CopVisualTransformation(),
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(onClick = { onAction(GoalDetailAction.OnContributionConfirm) }) {
                        Text(stringResource(R.string.goal_detail_contribution_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onAction(GoalDetailAction.OnContributionDismiss) }) {
                        Text(stringResource(R.string.action_cancel))
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
    innerPadding: PaddingValues,
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
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.goal_detail_progress_label),
                        style = MaterialTheme.typography.titleMedium
                    )
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        formatCop(goal.currentAmountCop),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        formatCop(goal.targetAmountCop),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    stringResource(R.string.goal_detail_section_details),
                    style = MaterialTheme.typography.titleMedium
                )
                InfoRow(
                    stringResource(R.string.goal_detail_remaining_label),
                    formatCop(goal.remainingCop)
                )
                InfoRow(stringResource(R.string.goal_detail_deadline_label), goal.deadlineDisplay)
                if (goal.requiredMonthlyCop != null) {
                    InfoRow(
                        stringResource(R.string.goal_detail_monthly_savings_label),
                        formatCop(goal.requiredMonthlyCop)
                    )
                }
                if (goal.isOverdue) {
                    Text(
                        stringResource(R.string.goal_detail_overdue_message),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (!goal.isOverdue && goal.progressPercent < 100f) {
            Button(
                onClick = { onAction(GoalDetailAction.OnAddContributionClick) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text(stringResource(R.string.goal_detail_add_contribution))
            }

            OutlinedButton(
                onClick = { onAction(GoalDetailAction.OnMarkAchievedClick) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text(stringResource(R.string.goal_detail_mark_achieved))
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

private fun formatCop(amount: Long): String =
    "$ " + NumberFormat.getNumberInstance(Locale("es", "CO")).format(amount)
