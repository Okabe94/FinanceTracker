package com.software.financetracker.navigation.feature

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.software.financetracker.feature.home.HomeScreenRoot
import com.software.financetracker.navigation.HomeRoute

fun NavGraphBuilder.homeNavGraph(navController: NavController) {
    composable<HomeRoute> { HomeScreenRoot(navController) }
}
