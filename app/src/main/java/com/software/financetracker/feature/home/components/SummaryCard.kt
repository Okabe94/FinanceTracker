package com.software.financetracker.feature.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.software.financetracker.R
import com.software.financetracker.ui.theme.Shapes
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SummaryCard(
    totalSpent: Long,
    totalLimit: Long,
    hasAnyLimit: Boolean,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        shape = Shapes.medium,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                stringResource(R.string.summary_card_title),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = formatCop(totalSpent),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            if (hasAnyLimit && totalLimit > 0) {
                val progress = (totalSpent.toFloat() / totalLimit).coerceIn(0f, 1f)
                val isOver = totalSpent > totalLimit
                LinearProgressIndicator(
                    progress = { progress },
                    color = if (isOver) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                )
                val available = totalLimit - totalSpent
                Text(
                    text = if (isOver) stringResource(
                        R.string.summary_card_over_limit,
                        formatCop(totalLimit)
                    )
                    else stringResource(
                        R.string.summary_card_available,
                        formatCop(available),
                        formatCop(totalLimit)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOver) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

internal fun formatCop(amount: Long): String =
    "$ " + NumberFormat.getNumberInstance(Locale("es", "CO")).format(amount)
