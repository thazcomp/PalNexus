package com.palhelper.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.palhelper.app.ui.theme.HudCyanAccent
import com.palhelper.app.ui.theme.PalHelperTheme

/**
 * A generic square, HUD-styled icon that loads an image live from [iconUrl] over the network,
 * falling back to a colorful badge with [fallbackLabel]'s first letter if there's no URL or the
 * image fails to load. Used for base-structure and material icons (mirrors [PalIcon], but square).
 */
@Composable
fun RemoteIcon(
    iconUrl: String?,
    fallbackLabel: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    accentColor: Color = HudCyanAccent
) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(placeholderColorFor(fallbackLabel))
            .border(2.dp, accentColor, shape),
        contentAlignment = Alignment.Center
    ) {
        if (iconUrl != null) {
            SubcomposeAsyncImage(
                model = iconUrl,
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                loading = { InitialBadge(fallbackLabel) },
                error = { InitialBadge(fallbackLabel) },
                modifier = Modifier.size(size)
            )
        } else {
            InitialBadge(fallbackLabel)
        }
    }
}

@Composable
private fun InitialBadge(label: String) {
    Text(
        text = label.take(1).uppercase(),
        style = MaterialTheme.typography.titleMedium,
        color = Color.White
    )
}

private val placeholderPalette = listOf(
    Color(0xFF1E88E5), Color(0xFF00897B), Color(0xFF8E24AA),
    Color(0xFFD81B60), Color(0xFFF4511E), Color(0xFF3949AB),
    Color(0xFF00ACC1), Color(0xFF7CB342)
)

private fun placeholderColorFor(key: String): Color =
    placeholderPalette[Math.floorMod(key.hashCode(), placeholderPalette.size)]

@Preview(showBackground = true, backgroundColor = 0xFF071B33, name = "RemoteIcon - com URL")
@Composable
private fun RemoteIconWithUrlPreview() {
    PalHelperTheme {
        RemoteIcon(
            iconUrl = "https://palworld.gg/images/items/T_itemicon_Material_Wood.png",
            fallbackLabel = "Wood",
            contentDescription = "Wood"
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF071B33, name = "RemoteIcon - placeholder")
@Composable
private fun RemoteIconPlaceholderPreview() {
    PalHelperTheme {
        RemoteIcon(iconUrl = null, fallbackLabel = "Stone", contentDescription = "Stone")
    }
}
