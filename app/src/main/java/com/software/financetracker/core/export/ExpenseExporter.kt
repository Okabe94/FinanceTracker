package com.software.financetracker.core.export

import com.software.financetracker.data.local.expense.TopExpenseRow

data class ExpenseExportRow(
    val date: String,
    val categoryName: String,
    val description: String,
    val amountCop: Long
)

object ExpenseExporter {

    fun toCsv(rows: List<ExpenseExportRow>): String {
        val sb = StringBuilder()
        sb.appendLine("Fecha,Categoría,Descripción,Monto (COP)")
        rows.forEach { row ->
            sb.append(escapeCsvField(row.date))
            sb.append(',')
            sb.append(escapeCsvField(row.categoryName))
            sb.append(',')
            sb.append(escapeCsvField(row.description))
            sb.append(',')
            sb.appendLine(row.amountCop.toString())
        }
        return sb.toString()
    }

    fun fromTopExpenseRows(rows: List<TopExpenseRow>): List<ExpenseExportRow> =
        rows.map { row ->
            ExpenseExportRow(
                date = row.date,
                categoryName = row.categoryName,
                description = row.description,
                amountCop = row.amountCop
            )
        }

    private fun escapeCsvField(value: String): String {
        val needsQuoting = value.contains(',') || value.contains('"') || value.contains('\n')
        return if (needsQuoting) "\"${value.replace("\"", "\"\"")}\"" else value
    }
}
