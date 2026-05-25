package com.software.financetracker.feature.category.form

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.software.financetracker.R
import com.software.financetracker.core.presentation.CopVisualTransformation
import com.software.financetracker.ui.components.iconForKey
import com.software.financetracker.ui.theme.Shapes

private val presetColors = listOf(
    0xFFE53935L, 0xFFF4511EL, 0xFFF59300L, 0xFFF6BF26L,
    0xFF33B679L, 0xFF0B8043L, 0xFF009688L, 0xFF039BE5L,
    0xFF3F51B5L, 0xFF7986CBL, 0xFFE67C73L, 0xFF616161L
).map { it.toInt() }

private val presetIcons = listOf(
    "shopping_cart", "restaurant", "directions_car", "home",
    "local_hospital", "school", "sports_esports", "flight",
    "local_cafe", "fitness_center", "pets", "phone",
    "checkroom", "celebrate", "music_note", "wifi",
    "savings", "work", "child_care", "more_horiz"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreen(
    state: CategoryFormState,
    onAction: (CategoryFormAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.categoryId == null) stringResource(R.string.category_form_title_new)
                        else stringResource(R.string.category_form_title_edit)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(CategoryFormAction.OnBackClick) }) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = state.name,
                onValueChange = { onAction(CategoryFormAction.OnNameChange(it)) },
                label = { Text(stringResource(R.string.label_name)) },
                isError = state.nameError != null,
                supportingText = { state.nameError?.let { Text(it.asString()) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(stringResource(R.string.label_color), style = MaterialTheme.typography.labelLarge)
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(presetColors) { argb ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(argb))
                            .clickable { onAction(CategoryFormAction.OnColorSelected(argb)) },
                        contentAlignment = Alignment.Center
                    ) {
                        val checkAnim by animateFloatAsState(
                            targetValue = if (argb == state.selectedColorArgb) 1f else 0f,
                            animationSpec = tween(durationMillis = 150),
                            label = "checkAnim"
                        )
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .graphicsLayer {
                                    alpha = checkAnim
                                    scaleX = 0.5f + checkAnim * 0.5f
                                    scaleY = 0.5f + checkAnim * 0.5f
                                }
                        )
                    }
                }
            }

            Text(stringResource(R.string.label_icon), style = MaterialTheme.typography.labelLarge)
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(presetIcons) { key ->
                    val selected = key == state.selectedIconKey
                    val bgColor by animateColorAsState(
                        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant,
                        animationSpec = tween(durationMillis = 200),
                        label = "iconBg"
                    )
                    val iconTint by animateColorAsState(
                        targetValue = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(durationMillis = 200),
                        label = "iconTint"
                    )
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(bgColor)
                            .clickable { onAction(CategoryFormAction.OnIconSelected(key)) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconForKey(key),
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.category_form_monthly_limit_label),
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = state.hasLimit,
                    onCheckedChange = { onAction(CategoryFormAction.OnLimitToggle(it)) }
                )
            }

            AnimatedVisibility(
                visible = state.hasLimit,
                enter = expandVertically(
                    animationSpec = tween(durationMillis = 250),
                    expandFrom = Alignment.Top
                ) + fadeIn(animationSpec = tween(durationMillis = 200)),
                exit = shrinkVertically(
                    animationSpec = tween(durationMillis = 200),
                    shrinkTowards = Alignment.Top
                ) + fadeOut(animationSpec = tween(durationMillis = 150))
            ) {
                OutlinedTextField(
                    value = state.limitInputCop,
                    onValueChange = {
                        onAction(CategoryFormAction.OnLimitAmountChange(it.filter { c -> c.isDigit() }))
                    },
                    label = { Text(stringResource(R.string.category_form_amount_cop_label)) },
                    prefix = { Text("$ ") },
                    isError = state.limitError != null,
                    supportingText = { state.limitError?.let { Text(it.asString()) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = CopVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onAction(CategoryFormAction.OnSaveClick) },
                modifier = Modifier.fillMaxWidth(),
                shape = Shapes.large,
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.action_save))
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
