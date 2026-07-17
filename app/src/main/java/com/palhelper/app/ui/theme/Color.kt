package com.palhelper.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Deep "game HUD" blues — the backbone of the new colorful window-panel look.
val HudNavyDark = Color(0xFF071B33)
val HudNavyMid = Color(0xFF0E2A4A)
val HudNavyPanel = Color(0xFF123A63)
val HudBluePrimary = Color(0xFF1E88E5)
val HudBlueLight = Color(0xFF64B5F6)
val HudCyanAccent = Color(0xFF00E5FF)
val HudGoldAccent = Color(0xFFFFC24B)
val HudOrangeAccent = Color(0xFFFF8A50)
val HudTextLight = Color(0xFFEAF6FF)
val HudTextMuted = Color(0xFFA9C6E8)

internal val PalHelperLightColors = lightColorScheme(
    primary = HudBluePrimary,
    onPrimary = Color.White,
    secondary = HudGoldAccent,
    onSecondary = HudNavyDark,
    tertiary = HudCyanAccent,
    background = Color(0xFFEAF3FB),
    onBackground = HudNavyDark,
    surface = Color(0xFFDCEBFA),
    onSurface = HudNavyDark,
    surfaceVariant = Color(0xFFC7DEF5),
    primaryContainer = Color(0xFFBBDEFB),
    secondaryContainer = Color(0xFFFFE3B8)
)

internal val PalHelperDarkColors = darkColorScheme(
    primary = HudBlueLight,
    onPrimary = HudNavyDark,
    secondary = HudGoldAccent,
    onSecondary = HudNavyDark,
    tertiary = HudCyanAccent,
    background = HudNavyDark,
    onBackground = HudTextLight,
    surface = HudNavyMid,
    onSurface = HudTextLight,
    surfaceVariant = HudNavyPanel,
    primaryContainer = Color(0xFF1E4A7A),
    secondaryContainer = Color(0xFF5A4523)
)
