package com.palhelper.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.palhelper.app.ui.components.GameWindowPanel
import com.palhelper.app.ui.theme.HudGoldAccent
import com.palhelper.app.ui.theme.HudNavyDark
import com.palhelper.app.ui.theme.HudNavyPanel
import com.palhelper.app.ui.theme.HudTextLight
import com.palhelper.app.ui.theme.HudTextMuted
import com.palhelper.app.ui.theme.PalHelperTheme

/**
 * Shown once, on app start, while every Pal icon preloads into Coil's cache
 * (see [com.palhelper.app.ui.preloadPalIcons]) — this is what avoids the picker/result screens
 * popping icons in one by one as the user scrolls or selects Pals.
 */
@Composable
fun PalIconLoadingScreen(
    loaded: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (total == 0) 0f else (loaded.toFloat() / total.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HudNavyDark)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameWindowPanel(accentColor = HudGoldAccent, cornerRadius = 20.dp) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🐾 Preparando os Pals...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = HudTextLight
                )
                Spacer(modifier = Modifier.size(16.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = HudGoldAccent,
                    trackColor = HudNavyPanel
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "$loaded / $total imagens carregadas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HudTextMuted
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Carregando - início")
@Composable
private fun PalIconLoadingScreenStartPreview() {
    PalHelperTheme {
        PalIconLoadingScreen(loaded = 0, total = 150)
    }
}

@Preview(showBackground = true, name = "Carregando - quase pronto")
@Composable
private fun PalIconLoadingScreenAlmostDonePreview() {
    PalHelperTheme {
        PalIconLoadingScreen(loaded = 132, total = 150)
    }
}
