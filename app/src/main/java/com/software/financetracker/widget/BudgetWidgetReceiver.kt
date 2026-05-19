package com.software.financetracker.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class BudgetWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = BudgetWidget()
}
