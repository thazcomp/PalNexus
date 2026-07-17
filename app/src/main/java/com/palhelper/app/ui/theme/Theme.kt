package com.palhelper.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * PalHelper always uses the dark, "game HUD" blue theme regardless of system setting —
 * it's a deliberate stylistic choice to evoke the look of Palworld's own in-game windows,
 * not a light/dark accessibility toggle.
 */
@Composable
fun PalHelperTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PalHelperDarkColors else PalHelperLightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PalHelperTypography,
        content = content
    )
}
