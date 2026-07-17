package com.palhelper.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.palhelper.app.data.model.Pal
import com.palhelper.app.ui.theme.HudCyanAccent
import com.palhelper.app.ui.theme.PalHelperTheme

/**
 * Shows a Pal's icon in a circular, HUD-styled frame. Icons are loaded live from
 * [Pal.iconUrl] (see [com.palhelper.app.data.PalDataSource]) over the network — nothing is
 * bundled into the app. If a Pal has no known icon URL, or the image fails to load, this
 * falls back to a colorful generated badge using the Pal's first letter, so the UI never
 * shows a broken image.
 */
@Composable
fun PalIcon(
    pal: Pal,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(placeholderColorFor(pal.id))
            .border(2.dp, HudCyanAccent, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        val iconUrl = pal.iconUrl
        if (iconUrl != null) {
            SubcomposeAsyncImage(
                model = iconUrl,
                contentDescription = pal.displayName,
                contentScale = ContentScale.Crop,
                loading = { PalInitialBadge(pal) },
                error = { PalInitialBadge(pal) },
                modifier = Modifier.size(size)
            )
        } else {
            PalInitialBadge(pal)
        }
    }
}

@Composable
private fun PalInitialBadge(pal: Pal) {
    Text(
        text = pal.displayName.take(1).uppercase(),
        style = MaterialTheme.typography.titleMedium,
        color = Color.White
    )
}

/** Deterministic color per Pal id, so the same Pal always gets the same placeholder color. */
private val placeholderPalette = listOf(
    Color(0xFF1E88E5), Color(0xFF00897B), Color(0xFF8E24AA),
    Color(0xFFD81B60), Color(0xFFF4511E), Color(0xFF3949AB),
    Color(0xFF00ACC1), Color(0xFF7CB342)
)

private fun placeholderColorFor(id: String): Color =
    placeholderPalette[Math.floorMod(id.hashCode(), placeholderPalette.size)]

@Preview(showBackground = true, backgroundColor = 0xFF071B33, name = "PalIcon - com URL")
@Composable
private fun PalIconWithUrlPreview() {
    PalHelperTheme {
        PalIcon(
            pal = Pal(
                "lamball", "Lamball", 1470, 252,
                iconUrl = "https://palworld.wiki.gg/images/thumb/Lamball_icon.png/128px-Lamball_icon.png?235903"
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF071B33, name = "PalIcon - sem URL (placeholder)")
@Composable
private fun PalIconPlaceholderPreview() {
    PalHelperTheme {
        PalIcon(pal = Pal("frostplume", "Frostplume", null, null))
    }
}
