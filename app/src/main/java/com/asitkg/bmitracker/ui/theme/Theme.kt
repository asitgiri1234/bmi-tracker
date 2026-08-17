package com.asitkg.bmitracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = Teal40,
    onPrimary = OnPrimaryLight,
    secondary = TealGrey40,
    onSecondary = OnPrimaryLight,
    tertiary = Sand40,
    onTertiary = OnPrimaryLight,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outlineVariant = OutlineVariantLight,
    error = ErrorRed,
    onError = OnErrorColor,
)

private val DarkColors = darkColorScheme(
    primary = Teal80,
    onPrimary = OnPrimaryDark,
    secondary = TealGrey80,
    onSecondary = OnPrimaryDark,
    tertiary = Sand80,
    onTertiary = OnPrimaryDark,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outlineVariant = OutlineVariantDark,
    error = ErrorRed,
    onError = OnErrorColor,
)

/**
 * @param dynamicColor honours the wallpaper-derived palette on Android 12+.
 * Disable it to force the brand palette, which is what the demo video uses so
 * the colours stay consistent across devices.
 */
@Composable
fun BmiTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BmiTypography,
        content = content,
    )
}
