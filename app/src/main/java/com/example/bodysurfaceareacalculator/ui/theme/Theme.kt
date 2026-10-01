package com.example.bodysurfaceareacalculator.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.bodysurfaceareacalculator.data.model.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = MedicalTealDark,
    onPrimary = OnMedicalTealDark,
    primaryContainer = MedicalTealContainerDark,
    onPrimaryContainer = OnMedicalTealContainerDark,
    secondary = SlateSecondaryDark,
    onSecondary = OnSlateSecondaryDark,
    secondaryContainer = SlateSecondaryContainerDark,
    onSecondaryContainer = OnSlateSecondaryContainerDark,
    tertiary = ClinicalTertiaryDark,
    onTertiary = OnClinicalTertiaryDark,
    tertiaryContainer = ClinicalTertiaryContainerDark,
    onTertiaryContainer = OnClinicalTertiaryContainerDark,
    background = NearBlackBackground,
    onBackground = SoftWhiteOnBackground,
    surface = GraphiteSurface,
    onSurface = SoftWhiteOnSurface,
    surfaceVariant = GraphiteSurfaceVariant,
    onSurfaceVariant = MutedTextOnSurfaceVariantDark,
    outline = ClinicalOutlineDark,
    outlineVariant = ClinicalOutlineVariantDark,
    error = MutedErrorRedDark
)

private val LightColorScheme = lightColorScheme(
    primary = MedicalTealLight,
    onPrimary = OnMedicalTealLight,
    primaryContainer = MedicalTealContainerLight,
    onPrimaryContainer = OnMedicalTealContainerLight,
    secondary = SlateSecondaryLight,
    onSecondary = OnSlateSecondaryLight,
    secondaryContainer = SlateSecondaryContainerLight,
    onSecondaryContainer = OnSlateSecondaryContainerLight,
    tertiary = ClinicalTertiaryLight,
    onTertiary = OnClinicalTertiaryLight,
    tertiaryContainer = ClinicalTertiaryContainerLight,
    onTertiaryContainer = OnClinicalTertiaryContainerLight,
    background = WarmWhiteBackground,
    onBackground = CharcoalOnBackground,
    surface = SoftGraySurface,
    onSurface = CharcoalOnSurface,
    surfaceVariant = SoftGraySurfaceVariant,
    onSurfaceVariant = MutedTextOnSurfaceVariant,
    outline = ClinicalOutlineLight,
    outlineVariant = ClinicalOutlineVariantLight,
    error = MutedErrorRedLight
)

@Composable
fun BodySurfaceAreaCalculatorTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> systemInDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}