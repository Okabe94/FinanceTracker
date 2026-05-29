package com.software.financetracker.navigation.feature

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.software.financetracker.feature.investment.detail.InvestmentDetailScreenRoot
import com.software.financetracker.feature.investment.entry.InvestmentEntryFormScreenRoot
import com.software.financetracker.feature.investment.entry.batch.BatchInvestmentEntryScreenRoot
import com.software.financetracker.feature.investment.form.InvestmentFormScreenRoot
import com.software.financetracker.feature.investment.list.InvestmentListScreenRoot
import com.software.financetracker.navigation.BatchInvestmentEntryRoute
import com.software.financetracker.navigation.InvestmentDetailRoute
import com.software.financetracker.navigation.InvestmentEntryFormRoute
import com.software.financetracker.navigation.InvestmentFormRoute
import com.software.financetracker.navigation.InvestmentListRoute

fun NavGraphBuilder.investmentNavGraph(navController: NavController) {
    composable<InvestmentListRoute> { InvestmentListScreenRoot(navController) }
    composable<InvestmentDetailRoute> { InvestmentDetailScreenRoot(navController) }
    composable<InvestmentFormRoute> { InvestmentFormScreenRoot(navController) }
    composable<InvestmentEntryFormRoute> { InvestmentEntryFormScreenRoot(navController) }
    composable<BatchInvestmentEntryRoute> { BatchInvestmentEntryScreenRoot(navController) }
}
