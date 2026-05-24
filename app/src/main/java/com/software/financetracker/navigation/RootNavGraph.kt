package com.software.financetracker.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.software.financetracker.navigation.feature.categoryNavGraph
import com.software.financetracker.navigation.feature.expenseNavGraph
import com.software.financetracker.navigation.feature.goalNavGraph
import com.software.financetracker.navigation.feature.homeNavGraph
import com.software.financetracker.navigation.feature.incomeNavGraph
import com.software.financetracker.navigation.feature.investmentNavGraph
import com.software.financetracker.navigation.feature.metricsNavGraph
import com.software.financetracker.navigation.feature.settingsNavGraph

@Composable
fun RootNavGraph(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute(HomeRoute::class) } == true,
                    onClick = {
                        navController.navigate(HomeRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null) },
                    label = { Text("Gastos") }
                )
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute(InvestmentListRoute::class) } == true,
                    onClick = {
                        navController.navigate(InvestmentListRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Rounded.TrendingUp, contentDescription = null) },
                    label = { Text("Inversiones") }
                )
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute(SettingsRoute::class) } == true,
                    onClick = {
                        navController.navigate(SettingsRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Rounded.Settings, contentDescription = null) },
                    label = { Text("Configuración") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                    fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = tween(300)) +
                    fadeOut(animationSpec = tween(150))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it / 3 }, animationSpec = tween(300)) +
                    fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                    fadeOut(animationSpec = tween(150))
            }
        ) {
            homeNavGraph(navController)
            categoryNavGraph(navController)
            expenseNavGraph(navController)
            metricsNavGraph(navController)
            investmentNavGraph(navController)
            incomeNavGraph(navController)
            goalNavGraph(navController)
            settingsNavGraph(navController)
        }
    }
}
