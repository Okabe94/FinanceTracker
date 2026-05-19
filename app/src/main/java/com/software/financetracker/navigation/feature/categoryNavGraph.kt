package com.software.financetracker.navigation.feature

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.software.financetracker.feature.category.detail.CategoryDetailScreenRoot
import com.software.financetracker.feature.category.form.CategoryFormScreenRoot
import com.software.financetracker.navigation.CategoryDetailRoute
import com.software.financetracker.navigation.CategoryFormRoute

fun NavGraphBuilder.categoryNavGraph(navController: NavController) {
    composable<CategoryDetailRoute> { CategoryDetailScreenRoot(navController) }
    composable<CategoryFormRoute> { CategoryFormScreenRoot(navController) }
}
