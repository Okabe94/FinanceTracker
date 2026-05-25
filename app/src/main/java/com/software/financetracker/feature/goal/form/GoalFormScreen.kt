package com.software.financetracker.feature.goal.form

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.software.financetracker.R
import com.software.financetracker.core.presentation.CopVisualTransformation
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val PRESET_COLORS = listOf(
    0xFF039BE5.toInt(), 0xFF43A047.toInt(), 0xFFE53935.toInt(),
    0xFFFB8C00.toInt(), 0xFF8E24AA.toInt(), 0xFF00ACC1.toInt()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalFormScreen(
    state: GoalFormState,
    onAction: (GoalFormAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.goalId == null) stringResource(R.string.goal_form_title_new) else stringResource(
                            R.string.goal_form_title_edit
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(GoalFormAction.OnBackClick) }) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = state.goalId != null,
                        enter = fadeIn(animationSpec = tween(200)) + scaleIn(
                            initialScale = 0.8f,
                            animationSpec = tween(
                                200
                            )
                        ),
                        exit = fadeOut(animationSpec = tween(150)) + scaleOut(
                            targetScale = 0.8f,
                            animationSpec = tween(
                                150
                            )
                        )
                    ) {
                        IconButton(onClick = { onAction(GoalFormAction.OnDeleteClick) }) {
                            Icon(
                                Icons.Rounded.Delete,
                                contentDescription = stringResource(R.string.action_delete)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { onAction(GoalFormAction.OnNameChange(it)) },
                label = { Text(stringResource(R.string.goal_form_name_label)) },
                placeholder = { Text(stringResource(R.string.goal_form_name_placeholder)) },
                isError = state.nameError,
                supportingText = if (state.nameError) {
                    { Text(stringResource(R.string.goal_form_name_error)) }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.targetAmountInput,
                onValueChange = { onAction(GoalFormAction.OnTargetAmountChange(it.filter { c -> c.isDigit() })) },
                label = { Text(stringResource(R.string.goal_form_target_amount_label)) },
                prefix = { Text("$ ") },
                isError = state.targetAmountError != null,
                supportingText = { state.targetAmountError?.let { Text(it.asString()) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CopVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(GoalFormAction.OnDateFieldClick) }) {
                OutlinedTextField(
                    value = state.displayDate,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.goal_form_date_label)) },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            Icons.Rounded.CalendarToday,
                            contentDescription = stringResource(R.string.cd_select_date)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Text(stringResource(R.string.label_color), style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PRESET_COLORS.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(color))
                            .then(
                                if (state.selectedColorArgb == color)
                                    Modifier.border(
                                        3.dp,
                                        MaterialTheme.colorScheme.onSurface,
                                        CircleShape
                                    )
                                else Modifier
                            )
                            .clickable { onAction(GoalFormAction.OnColorSelected(color)) }
                    )
                }
            }

            if (state.showDeleteConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { onAction(GoalFormAction.OnDeleteDismiss) },
                    title = { Text(stringResource(R.string.goal_form_delete_title)) },
                    text = { Text(stringResource(R.string.goal_form_delete_message)) },
                    confirmButton = {
                        TextButton(onClick = { onAction(GoalFormAction.OnDeleteConfirm) }) {
                            Text(
                                stringResource(R.string.action_delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { onAction(GoalFormAction.OnDeleteDismiss) }) {
                            Text(
                                stringResource(R.string.action_cancel)
                            )
                        }
                    }
                )
            }

            if (state.showDatePicker) {
                val initialMillis = remember(state.selectedDateStorage) {
                    LocalDate.parse(
                        state.selectedDateStorage,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    )
                        .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                }
                val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
                DatePickerDialog(
                    onDismissRequest = { onAction(GoalFormAction.OnDatePickerDismiss) },
                    confirmButton = {
                        TextButton(onClick = {
                            pickerState.selectedDateMillis?.let {
                                onAction(
                                    GoalFormAction.OnDateSelected(
                                        it
                                    )
                                )
                            }
                        }) { Text(stringResource(R.string.action_ok)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { onAction(GoalFormAction.OnDatePickerDismiss) }) {
                            Text(
                                stringResource(R.string.action_cancel)
                            )
                        }
                    }
                ) {
                    DatePicker(
                        state = pickerState,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onAction(GoalFormAction.OnSaveClick) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp), strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.action_save))
                }
            }
        }
    }
}
