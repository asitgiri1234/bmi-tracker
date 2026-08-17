package com.asitkg.bmitracker.ui.theme

import androidx.compose.ui.graphics.Color

// Brand palette — a calm teal/green reads as "health" without the clinical
// feel of pure blue, and stays legible against the BMI category colours below.
val Teal40 = Color(0xFF00696D)
val Teal80 = Color(0xFF4FD8DF)
val TealGrey40 = Color(0xFF4A6365)
val TealGrey80 = Color(0xFFB1CBCD)
val Sand40 = Color(0xFF4C5C92)
val Sand80 = Color(0xFFB6C4FF)

val SurfaceLight = Color(0xFFFAFDFC)
val SurfaceDark = Color(0xFF0E1415)
val ErrorRed = Color(0xFFBA1A1A)

// BMI category colours. Deliberately distinguishable in both themes and
// ordered cool -> warm so the gauge reads intuitively.
val CategoryUnderweight = Color(0xFF4B9FE1)
val CategoryNormal = Color(0xFF2E9E63)
val CategoryOverweight = Color(0xFFE0A32E)
val CategoryObese = Color(0xFFD9534F)
