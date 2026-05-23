package com.software.financetracker.core.export

import com.software.financetracker.core.util.CurrencyHelper
import com.software.financetracker.data.local.investment.InvestmentEntryEntity

object InvestmentExporter {

    fun toCsv(investmentName: String, currency: String, entries: List<InvestmentEntryEntity>): String {
        val sb = StringBuilder()
        sb.appendLine("id,nombre_inversion,moneda,tipo_entrada,monto_unidades_menores,monto_display,fecha,notas")
        entries.forEach { entry ->
            sb.append(entry.id)
            sb.append(',')
            sb.append(escapeCsvField(investmentName))
            sb.append(',')
            sb.append(escapeCsvField(currency))
            sb.append(',')
            sb.append(escapeCsvField(entry.entryType))
            sb.append(',')
            sb.append(entry.amountMinorUnits)
            sb.append(',')
            sb.append(escapeCsvField(CurrencyHelper.format(entry.amountMinorUnits, currency)))
            sb.append(',')
            sb.append(escapeCsvField(entry.date))
            sb.append(',')
            sb.appendLine(escapeCsvField(entry.notes))
        }
        return sb.toString()
    }

    private fun escapeCsvField(value: String): String {
        val needsQuoting = value.contains(',') || value.contains('"') || value.contains('\n')
        return if (needsQuoting) "\"${value.replace("\"", "\"\"")}\"" else value
    }
}
