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

val ErrorRed = Color(0xFFBA1A1A)

// BMI category colours. Deliberately distinguishable in both themes and
// ordered cool -> warm so the gauge reads intuitively.
val CategoryUnderweight = Color(0xFF4B9FE1)
val CategoryNormal = Color(0xFF2E9E63)
val CategoryOverweight = Color(0xFFE0A32E)
val CategoryObese = Color(0xFFD9534F)

// Every Material role used by any component is set explicitly below.
//
// Material 3 fills any role left unspecified from its own baseline palette,
// which is purple. A half-defined scheme therefore leaks purple into whichever
// component happens to read the missing role — button labels via onPrimary,
// selected segmented buttons via secondaryContainer, and plain Cards via the
// surfaceContainer family. Defining the whole set is the only reliable fix.

// --- Light ---
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFF9CF1F0)
val OnPrimaryContainerLight = Color(0xFF002020)
val SecondaryContainerLight = Color(0xFFCCE8E7)
val OnSecondaryContainerLight = Color(0xFF051F1F)
val TertiaryContainerLight = Color(0xFFDDE1FF)
val OnTertiaryContainerLight = Color(0xFF001945)

val BackgroundLight = Color(0xFFFAFDFC)
val OnSurfaceLight = Color(0xFF191C1C)
val SurfaceVariantLight = Color(0xFFDAE5E4)
val OnSurfaceVariantLight = Color(0xFF3F4948)
val OutlineLight = Color(0xFF6F7979)
val OutlineVariantLight = Color(0xFFBEC9C8)

val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
val SurfaceContainerLowLight = Color(0xFFF4FAF9)
val SurfaceContainerLight = Color(0xFFEEF5F4)
val SurfaceContainerHighLight = Color(0xFFE8EFEE)
val SurfaceContainerHighestLight = Color(0xFFE2E9E8)
val SurfaceDimLight = Color(0xFFDBE1E0)
val SurfaceBrightLight = Color(0xFFFAFDFC)
val InverseSurfaceLight = Color(0xFF2B3231)
val InverseOnSurfaceLight = Color(0xFFECF2F1)

// --- Dark ---
val OnPrimaryDark = Color(0xFF00363A)
val PrimaryContainerDark = Color(0xFF004F52)
val OnPrimaryContainerDark = Color(0xFF9CF1F0)
val SecondaryContainerDark = Color(0xFF324B4B)
val OnSecondaryContainerDark = Color(0xFFCCE8E7)
val TertiaryContainerDark = Color(0xFF344578)
val OnTertiaryContainerDark = Color(0xFFDDE1FF)

val BackgroundDark = Color(0xFF0E1415)
val OnSurfaceDark = Color(0xFFE0E3E2)
val SurfaceVariantDark = Color(0xFF3F4948)
val OnSurfaceVariantDark = Color(0xFFBEC9C8)
val OutlineDark = Color(0xFF899393)
val OutlineVariantDark = Color(0xFF3F4948)

val SurfaceContainerLowestDark = Color(0xFF090F10)
val SurfaceContainerLowDark = Color(0xFF171D1D)
val SurfaceContainerDark = Color(0xFF1B2121)
val SurfaceContainerHighDark = Color(0xFF252B2C)
val SurfaceContainerHighestDark = Color(0xFF303636)
val SurfaceDimDark = Color(0xFF0E1415)
val SurfaceBrightDark = Color(0xFF343A3A)
val InverseSurfaceDark = Color(0xFFE0E3E2)
val InverseOnSurfaceDark = Color(0xFF2B3231)

val OnErrorColor = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFFFDAD6)
val OnErrorContainerLight = Color(0xFF410002)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD6)
