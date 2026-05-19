package com.software.financetracker.feature.category.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.software.financetracker.core.presentation.ObserveAsEvents
import com.software.financetracker.navigation.CategoryFormRoute
import com.software.financetracker.navigation.ExpenseFormRoute
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CategoryDetailScreenRoot(navController: NavController) {
    val viewModel: CategoryDetailViewModel = koinViewModel()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            CategoryDetailEvent.NavigateBack -> navController.navigateUp()
            is CategoryDetailEvent.NavigateToEditCategory ->
                navController.navigate(CategoryFormRoute(event.categoryId))
            is CategoryDetailEvent.NavigateToAddExpense ->
                navController.navigate(ExpenseFormRoute(event.categoryId, null))
            is CategoryDetailEvent.NavigateToEditExpense ->
                navController.navigate(ExpenseFormRoute(event.categoryId, event.expenseId))
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    CategoryDetailScreen(state = state, onAction = viewModel::onAction)
}
