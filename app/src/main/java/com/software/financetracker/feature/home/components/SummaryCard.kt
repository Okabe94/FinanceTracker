package com.software.financetracker.feature.home.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.software.financetracker.ui.theme.Shapes
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SummaryCard(
    totalSpent: Long,
    totalLimit: Long,
    hasAnyLimit: Boolean,
    totalIncomeCop: Long = 0L,
    netBalanceCop: Long = 0L,
    hasIncomeData: Boolean = false,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = Shapes.medium,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            Text("Resumen del mes", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Total gastado: ${formatCop(totalSpent)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            if (hasIncomeData) {
                Text(
                    text = "Total ingresos: ${formatCop(totalIncomeCop)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                val balanceColor = if (netBalanceCop >= 0L) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.error
                Text(
                    text = "Balance neto: ${formatCop(netBalanceCop)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = balanceColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (hasAnyLimit && totalLimit > 0) {
                val progress = (totalSpent.toFloat() / totalLimit).coerceIn(0f, 1f)
                val isOver = totalSpent > totalLimit
                LinearProgressIndicator(
                    progress = { progress },
                    color = if (isOver) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth().clip(CircleShape)
                )
                Text(
                    text = "Límite total: ${formatCop(totalLimit)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

internal fun formatCop(amount: Long): String =
    "$ " + NumberFormat.getNumberInstance(Locale("es", "CO")).format(amount)
