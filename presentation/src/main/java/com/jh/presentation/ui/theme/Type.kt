package com.jh.presentation.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

val Typography = Typography(
    defaultFontFamily = pretendard,
    h1 = TextStyle(
        fontSize = 60.sp,
        letterSpacing = 0.sp,
        fontWeight = Bold
    ),
    h2 = TextStyle(
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    h3 = TextStyle(
        fontSize = 24.sp,
        letterSpacing = 0.sp,
        fontWeight = Bold
    ),
    h4 = TextStyle(
        fontSize = 18.sp,
        letterSpacing = 0.sp,
        fontWeight = Bold
    ),
    h5 = TextStyle(
        fontSize = 36.sp,
        letterSpacing = 0.sp,
        fontWeight = Bold
    ),
    h6 = TextStyle(
        fontSize = 36.sp,
        letterSpacing = 0.sp,
        fontWeight = Bold,
        textAlign = TextAlign.Center
    ),
    subtitle1 = TextStyle(
        fontSize = 16.sp,
        letterSpacing = 0.sp,
        fontWeight = Medium
    ),
    body1 = TextStyle(
        fontSize = 14.sp,
        letterSpacing = 0.sp,
        fontWeight = Medium
    ),
    body2 = TextStyle(
        fontSize = 18.sp,
        letterSpacing = 0.sp,
        fontWeight = Medium
    )
)