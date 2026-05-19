package com.software.financetracker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// SoDoSans is proprietary; system sans-serif with tight -0.16px tracking is the closest match
private val SoDoSansFallback = FontFamily.SansSerif
private val TightTracking = (-0.16).sp

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = SoDoSansFallback,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = TightTracking,
    ),
    displayMedium = TextStyle(
        fontFamily = SoDoSansFallback,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = TightTracking,
    ),
    displaySmall = TextStyle(
        fontFamily = SoDoSansFallback,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = TightTracking,
    ),
    headlineLarge = TextStyle(
        fontFamily = SoDoSansFallback,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = TightTracking,
    ),
    headlineMedium = TextStyle(
        fontFamily = SoDoSansFallback,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = TightTracking,
    ),
    headlineSmall = TextStyle(
        fontFamily = SoDoSansFallback,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = TightTracking,
    ),
    titleLarge = TextStyle(
        fontFamily = SoDoSansFallback,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = TightTracking,
    ),
    titleMedium = TextStyle(
        fontFamily = SoDoSansFallback,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = TightTracking,
    ),
    titleSmall = TextStyle(
        fontFamily = SoDoSansFallback,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = TightTracking,
    ),
    bodyLarge = TextStyle(
        fontFamily = SoDoSansFallback,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = TightTracking,
    ),
    bodyMedium = TextStyle(
        fontFamily = SoDoSansFallback,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = TightTracking,
    ),
    bodySmall = TextStyle(
        fontFamily = SoDoSansFallback,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = TightTracking,
    ),
    labelLarge = TextStyle(
        fontFamily = SoDoSansFallback,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = TightTracking,
    ),
    labelMedium = TextStyle(
        fontFamily = SoDoSansFallback,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = TightTracking,
    ),
    labelSmall = TextStyle(
        fontFamily = SoDoSansFallback,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = TightTracking,
    ),
)
