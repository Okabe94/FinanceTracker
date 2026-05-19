package com.software.financetracker.core.presentation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CopVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val formatted = digits.reversed().chunked(3).joinToString(".").reversed()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var dotsInserted = 0
                var j = 0
                for (i in 0 until offset) {
                    while (j < formatted.length && formatted[j] == '.') {
                        dotsInserted++
                        j++
                    }
                    j++
                }
                while (j < formatted.length && formatted[j] == '.') {
                    dotsInserted++
                    j++
                }
                return offset + dotsInserted
            }

            override fun transformedToOriginal(offset: Int): Int =
                (offset - formatted.take(offset).count { it == '.' })
                    .coerceIn(0, digits.length)
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
