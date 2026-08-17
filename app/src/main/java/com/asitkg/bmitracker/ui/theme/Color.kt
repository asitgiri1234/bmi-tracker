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

// Foreground roles. These must be set explicitly: Material 3 fills any role
// left unspecified from its own baseline palette, which is purple, so a
// half-defined scheme leaks purple text onto brand-coloured buttons.
val OnPrimaryLight = Color(0xFFFFFFFF)
val OnPrimaryDark = Color(0xFF00363A)
val OnSurfaceLight = Color(0xFF191C1C)
val OnSurfaceDark = Color(0xFFE0E3E2)
val SurfaceVariantLight = Color(0xFFDAE5E4)
val SurfaceVariantDark = Color(0xFF3F4948)
val OnSurfaceVariantLight = Color(0xFF3F4948)
val OnSurfaceVariantDark = Color(0xFFBEC9C8)
val OutlineVariantLight = Color(0xFFBEC9C8)
val OutlineVariantDark = Color(0xFF3F4948)
val OnErrorColor = Color(0xFFFFFFFF)

// Container roles. Components such as SegmentedButton colour their selected
// state from *Container rather than from the base role, so these must be set
// too or the same purple baseline leaks through.
val PrimaryContainerLight = Color(0xFF9CF1F0)
val OnPrimaryContainerLight = Color(0xFF002020)
val SecondaryContainerLight = Color(0xFFCCE8E7)
val OnSecondaryContainerLight = Color(0xFF051F1F)
val TertiaryContainerLight = Color(0xFFDDE1FF)
val OnTertiaryContainerLight = Color(0xFF001945)

val PrimaryContainerDark = Color(0xFF004F52)
val OnPrimaryContainerDark = Color(0xFF9CF1F0)
val SecondaryContainerDark = Color(0xFF324B4B)
val OnSecondaryContainerDark = Color(0xFFCCE8E7)
val TertiaryContainerDark = Color(0xFF344578)
val OnTertiaryContainerDark = Color(0xFFDDE1FF)

// BMI category colours. Deliberately distinguishable in both themes and
// ordered cool -> warm so the gauge reads intuitively.
val CategoryUnderweight = Color(0xFF4B9FE1)
val CategoryNormal = Color(0xFF2E9E63)
val CategoryOverweight = Color(0xFFE0A32E)
val CategoryObese = Color(0xFFD9534F)
