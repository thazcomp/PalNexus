package com.palhelper.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.palhelper.app.ui.theme.HudCyanAccent
import com.palhelper.app.ui.theme.HudGoldAccent
import com.palhelper.app.ui.theme.HudNavyMid
import com.palhelper.app.ui.theme.HudNavyPanel
import com.palhelper.app.ui.theme.PalHelperTheme

/**
 * A stylized "game HUD window" panel: a blue gradient card with corner-bracket accents,
 * evoking the tech/scanner-style windows Palworld itself uses for its UI.
 *
 * @param accentColor The color of the border and corner brackets.
 */
@Composable
fun GameWindowPanel(
    modifier: Modifier = Modifier,
    accentColor: Color = HudCyanAccent,
    cornerRadius: Dp = 16.dp,
    contentPadding: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(HudNavyPanel, HudNavyMid)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundBrush)
            .drawWithContent {
                drawContent()
                drawCornerBrackets(accentColor)
            }
            .padding(contentPadding)
    ) {
        content()
    }
}

private fun DrawScope.drawCornerBrackets(color: Color) {
    val bracketLength = 18.dp.toPx()
    val inset = 6.dp.toPx()
    val strokeWidth = 3.dp.toPx()
    val w = size.width
    val h = size.height

    // Top-left
    drawLine(color, Offset(inset, inset), Offset(inset + bracketLength, inset), strokeWidth)
    drawLine(color, Offset(inset, inset), Offset(inset, inset + bracketLength), strokeWidth)

    // Top-right
    drawLine(color, Offset(w - inset, inset), Offset(w - inset - bracketLength, inset), strokeWidth)
    drawLine(color, Offset(w - inset, inset), Offset(w - inset, inset + bracketLength), strokeWidth)

    // Bottom-left
    drawLine(color, Offset(inset, h - inset), Offset(inset + bracketLength, h - inset), strokeWidth)
    drawLine(color, Offset(inset, h - inset), Offset(inset, h - inset - bracketLength), strokeWidth)

    // Bottom-right
    drawLine(color, Offset(w - inset, h - inset), Offset(w - inset - bracketLength, h - inset), strokeWidth)
    drawLine(color, Offset(w - inset, h - inset), Offset(w - inset, h - inset - bracketLength), strokeWidth)
}

@Preview(showBackground = true, backgroundColor = 0xFF071B33, name = "GameWindowPanel - ciano")
@Composable
private fun GameWindowPanelCyanPreview() {
    PalHelperTheme {
        GameWindowPanel(accentColor = HudCyanAccent) {
            Text(text = "Painel estilo HUD do jogo", color = Color.White)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF071B33, name = "GameWindowPanel - dourado")
@Composable
private fun GameWindowPanelGoldPreview() {
    PalHelperTheme {
        GameWindowPanel(accentColor = HudGoldAccent) {
            Text(text = "Resultado do cruzamento", color = Color.White)
        }
    }
}
