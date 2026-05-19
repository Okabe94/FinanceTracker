package com.software.financetracker.navigation.feature

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.software.financetracker.feature.metrics.MetricsScreenRoot
import com.software.financetracker.navigation.MetricsRoute

fun NavGraphBuilder.metricsNavGraph(navController: NavController) {
    composable<MetricsRoute> { MetricsScreenRoot(navController) }
}
