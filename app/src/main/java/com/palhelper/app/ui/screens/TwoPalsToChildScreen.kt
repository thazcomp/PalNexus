package com.palhelper.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.palhelper.app.data.model.Pal
import com.palhelper.app.ui.components.GameWindowPanel
import com.palhelper.app.ui.components.PalIcon
import com.palhelper.app.ui.components.PalPickerField
import com.palhelper.app.ui.theme.HudGoldAccent
import com.palhelper.app.ui.theme.HudNavyDark
import com.palhelper.app.ui.theme.HudTextLight
import com.palhelper.app.ui.theme.HudTextMuted
import com.palhelper.app.ui.theme.PalHelperTheme

/**
 * Mode 1: pick two parent Pals and see the resulting child, following Palworld's real
 * breeding formula (Combi Rank averaging + special combos).
 */
@Composable
fun TwoPalsToChildScreen(
    allPals: List<Pal>,
    parentA: Pal?,
    parentB: Pal?,
    computedChild: Pal?,
    onParentASelected: (Pal) -> Unit,
    onParentBSelected: (Pal) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HudNavyDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Escolha os dois Pals para cruzar",
            style = MaterialTheme.typography.titleMedium,
            color = HudTextLight
        )

        PalPickerField(
            label = "Pal 1 (pai/mãe A)",
            allPals = allPals,
            selected = parentA,
            onPalSelected = onParentASelected
        )

        PalPickerField(
            label = "Pal 2 (pai/mãe B)",
            allPals = allPals,
            selected = parentB,
            onPalSelected = onParentBSelected
        )

        if (parentA != null && parentB != null) {
            GameWindowPanel(accentColor = HudGoldAccent, cornerRadius = 20.dp) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Resultado do cruzamento",
                        style = MaterialTheme.typography.labelLarge,
                        color = HudTextMuted
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    if (computedChild != null) {
                        PalIcon(pal = computedChild, size = 80.dp)
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                    Text(
                        text = computedChild?.displayName ?: "Combinação desconhecida",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = HudGoldAccent
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onClear,
                    colors = ButtonDefaults.buttonColors(containerColor = HudGoldAccent)
                ) {
                    Text("Limpar seleção", color = HudNavyDark)
                }
            }
        }
    }
}

private val previewLamball = Pal(
    "lamball", "Lamball", 1470, 252,
    iconUrl = "https://palworld.wiki.gg/images/thumb/Lamball_icon.png/128px-Lamball_icon.png?235903"
)
private val previewCattiva = Pal(
    "cattiva", "Cattiva", 1460, 271,
    iconUrl = "https://palworld.wiki.gg/images/thumb/Cattiva_icon.png/128px-Cattiva_icon.png?31e506"
)
private val previewChikipi = Pal(
    "chikipi", "Chikipi", 1500, 288,
    iconUrl = "https://palworld.wiki.gg/images/thumb/Chikipi_icon.png/128px-Chikipi_icon.png?59a9df"
)
private val previewPals = listOf(previewLamball, previewCattiva, previewChikipi)

@Preview(showBackground = true, name = "Sem seleção")
@Composable
private fun TwoPalsToChildScreenEmptyPreview() {
    PalHelperTheme {
        TwoPalsToChildScreen(
            allPals = previewPals,
            parentA = null,
            parentB = null,
            computedChild = null,
            onParentASelected = {},
            onParentBSelected = {},
            onClear = {}
        )
    }
}

@Preview(showBackground = true, name = "Com resultado")
@Composable
private fun TwoPalsToChildScreenResultPreview() {
    PalHelperTheme {
        TwoPalsToChildScreen(
            allPals = previewPals,
            parentA = previewLamball,
            parentB = previewCattiva,
            computedChild = previewChikipi,
            onParentASelected = {},
            onParentBSelected = {},
            onClear = {}
        )
    }
}
