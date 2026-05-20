package com.software.financetracker.core.presentation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class DecimalVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val input = text.text
        val dotIdx = input.indexOf('.')
        val intPart = if (dotIdx >= 0) input.substring(0, dotIdx) else input
        val fracPart = if (dotIdx >= 0) input.substring(dotIdx) else ""

        val formattedInt = if (intPart.isEmpty()) ""
        else intPart.reversed().chunked(3).joinToString(",").reversed()
        val formatted = formattedInt + fracPart

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return if (offset <= intPart.length) {
                    var commas = 0
                    var j = 0
                    for (i in 0 until offset) {
                        while (j < formattedInt.length && formattedInt[j] == ',') {
                            commas++
                            j++
                        }
                        j++
                    }
                    while (j < formattedInt.length && formattedInt[j] == ',') {
                        commas++
                        j++
                    }
                    offset + commas
                } else {
                    offset + formattedInt.count { it == ',' }
                }
            }

            override fun transformedToOriginal(offset: Int): Int =
                (if (offset <= formattedInt.length) {
                    offset - formatted.take(offset).count { it == ',' }
                } else {
                    offset - formattedInt.count { it == ',' }
                }).coerceIn(0, input.length)
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
