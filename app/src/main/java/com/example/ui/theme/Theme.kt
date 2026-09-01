package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = VibrantPurple80,
    onPrimary = Color(0xFF381E72),
    primaryContainer = VibrantLavenderContainerDark,
    onPrimaryContainer = VibrantLavenderOnContainerDark,
    secondary = VibrantSunsetOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF5A2500),
    onSecondaryContainer = VibrantSunsetContainer,
    tertiary = VibrantSkyBlueAccent,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF00497D),
    onTertiaryContainer = VibrantSkyBlueContainer,
    background = CanvasWarmDark,
    onBackground = Color(0xFFE6E1E5),
    surface = SurfaceWarmDark,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = SurfaceWarmVariantDark,
    onSurfaceVariant = Color(0xFFCAC4D0)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = VibrantPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = VibrantLavenderContainer,
    onPrimaryContainer = VibrantLavenderOnContainer,
    secondary = VibrantSunsetOrange,
    onSecondary = Color.White,
    secondaryContainer = VibrantSunsetContainer,
    onSecondaryContainer = VibrantSunsetOnContainer,
    tertiary = VibrantSkyBlueAccent,
    onTertiary = Color.White,
    tertiaryContainer = VibrantSkyBlueContainer,
    onTertiaryContainer = VibrantSkyBlueOnContainer,
    background = CanvasWarmLight,
    onBackground = Color(0xFF1D1B20),
    surface = SurfaceWarmLight,
    onSurface = Color(0xFF1D1B20),
    surfaceVariant = SurfaceWarmVariant,
    onSurfaceVariant = Color(0xFF49454F)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
