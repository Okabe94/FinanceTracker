package com.software.financetracker.navigation.feature

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.software.financetracker.feature.settings.SettingsScreenRoot
import com.software.financetracker.navigation.SettingsRoute

fun NavGraphBuilder.settingsNavGraph(navController: NavController) {
    composable<SettingsRoute> { SettingsScreenRoot(navController) }
}
