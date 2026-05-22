package com.software.financetracker.feature.expense.assistant

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.software.financetracker.core.presentation.CopVisualTransformation
import com.software.financetracker.ui.components.iconForKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantExpenseScreen(
    state: AssistantExpenseState,
    onAction: (AssistantExpenseAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar gasto") },
                navigationIcon = {
                    IconButton(onClick = { onAction(AssistantExpenseAction.OnBackClick) }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.amountInput,
                onValueChange = {
                    onAction(AssistantExpenseAction.OnAmountChange(it.filter { c -> c.isDigit() }))
                },
                label = { Text("Monto (COP)") },
                prefix = { Text("$ ") },
                isError = state.amountError != null,
                supportingText = { state.amountError?.let { Text(it.asString()) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CopVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Categoría",
                style = MaterialTheme.typography.labelLarge,
                color = if (state.categoryError != null) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (state.categoryError != null) {
                Text(
                    text = state.categoryError.asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            state.categories.chunked(3).forEach { rowCategories ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowCategories.forEach { category ->
                        FilterChip(
                            selected = state.selectedCategory?.id == category.id,
                            onClick = { onAction(AssistantExpenseAction.OnCategorySelected(category)) },
                            label = { Text(category.name, maxLines = 1) },
                            leadingIcon = {
                                Icon(
                                    imageVector = iconForKey(category.iconKey),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onAction(AssistantExpenseAction.OnSaveClick) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar")
                }
            }
        }
    }
}
