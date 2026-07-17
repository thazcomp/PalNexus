package com.palhelper.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.palhelper.app.ui.components.GameWindowPanel
import com.palhelper.app.ui.theme.HudCyanAccent
import com.palhelper.app.ui.theme.HudGoldAccent
import com.palhelper.app.ui.theme.HudNavyDark
import com.palhelper.app.ui.theme.HudTextLight
import com.palhelper.app.ui.theme.HudTextMuted
import com.palhelper.app.ui.theme.PalHelperTheme

/** The two tools the app offers, chosen from the home menu. */
enum class PalHelperDestination {
    BREEDING,
    RESOURCES
}

/**
 * Home menu shown before either tool. The user picks between the breeding calculator and the
 * base-building resource calculator via two large HUD-styled cards.
 */
@Composable
fun HomeMenuScreen(
    onSelect: (PalHelperDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HudNavyDark)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🐾 PalHelper",
            style = MaterialTheme.typography.titleLarge,
            color = HudTextLight,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Escolha uma ferramenta",
            style = MaterialTheme.typography.bodyLarge,
            color = HudTextMuted
        )

        Spacer(modifier = Modifier.size(8.dp))

        MenuCard(
            emoji = "🥚",
            title = "Calculadora de Breeding",
            subtitle = "Descubra o filho de dois Pals, ou os pais de um Pal",
            accentColor = HudGoldAccent,
            onClick = { onSelect(PalHelperDestination.BREEDING) }
        )

        MenuCard(
            emoji = "🏗️",
            title = "Calculadora de Recursos",
            subtitle = "Some os materiais para construir toda a sua base",
            accentColor = HudCyanAccent,
            onClick = { onSelect(PalHelperDestination.RESOURCES) }
        )
    }
}

@Composable
private fun MenuCard(
    emoji: String,
    title: String,
    subtitle: String,
    accentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    GameWindowPanel(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        accentColor = accentColor,
        cornerRadius = 20.dp,
        contentPadding = 20.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.size(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = HudTextLight,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = HudTextMuted
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "HomeMenuScreen")
@Composable
private fun HomeMenuScreenPreview() {
    PalHelperTheme {
        HomeMenuScreen(onSelect = {})
    }
}
