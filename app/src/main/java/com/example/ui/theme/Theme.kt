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

private val DarkColorScheme = darkColorScheme(
  primary = DarcorpBrown500,
  onPrimary = Color.White,
  primaryContainer = DarcorpBrown800,
  onPrimaryContainer = DarcorpBrownCream,
  secondary = DarcorpLightBluePrimary,
  onSecondary = Color.White,
  secondaryContainer = DarcorpBrown900,
  onSecondaryContainer = Color.White,
  tertiary = DarcorpEmerald,
  onTertiary = Color.White,
  background = Color(0xFF0F172A),
  onBackground = DarcorpLightBlue100,
  surface = Color(0xFF1E293B),
  onSurface = Color.White,
  surfaceVariant = DarcorpBrown900,
  onSurfaceVariant = DarcorpLightBlue100,
  outline = DarcorpBrown700,
  error = DarcorpCrimson
)

private val LightColorScheme = lightColorScheme(
  primary = DarcorpBrown700, // Rich Warm Brown
  onPrimary = Color.White,
  primaryContainer = DarcorpBrown800, // Brown bar
  onPrimaryContainer = DarcorpBrownCream,
  secondary = DarcorpLightBluePrimary, // Light Blue accent
  onSecondary = Color.White,
  secondaryContainer = DarcorpLightBlue100, // Light Blue tint
  onSecondaryContainer = DarcorpBrown900,
  tertiary = DarcorpEmeraldDark,
  onTertiary = Color.White,
  background = DarcorpLightBlueBg, // Biru Muda background (#F0F9FF)
  onBackground = DarcorpBrown900,
  surface = Color.White, // Putih cards (#FFFFFF)
  onSurface = DarcorpBrown900,
  surfaceVariant = DarcorpLightBlue100,
  onSurfaceVariant = DarcorpSlate700,
  outline = DarcorpLightBlue200,
  error = DarcorpCrimson
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color so our custom Light Blue, White, and Brown theme stays active
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
