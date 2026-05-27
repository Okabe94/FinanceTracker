package com.software.financetracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.software.financetracker.R
import com.software.financetracker.ui.theme.GreenAccent
import com.software.financetracker.ui.theme.Shapes

@Composable
fun FinanceTrackerFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        shape = Shapes.medium,
        containerColor = GreenAccent,
        contentColor = Color.White,
        modifier = modifier
    ) {
        Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.fab_add_cd))
    }
}
