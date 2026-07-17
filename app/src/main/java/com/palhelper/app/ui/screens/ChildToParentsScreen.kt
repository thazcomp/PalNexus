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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.palhelper.app.data.model.Pal
import com.palhelper.app.ui.components.GameWindowPanel
import com.palhelper.app.ui.components.PalIcon
import com.palhelper.app.ui.components.PalPickerField
import com.palhelper.app.ui.theme.HudCyanAccent
import com.palhelper.app.ui.theme.HudGoldAccent
import com.palhelper.app.ui.theme.HudNavyDark
import com.palhelper.app.ui.theme.HudTextLight
import com.palhelper.app.ui.theme.HudTextMuted
import com.palhelper.app.ui.theme.PalHelperTheme

/**
 * Mode 2: pick a single target Pal and see every parent pair that produces it — special
 * combos, the same-species pair, and every formula-based pairing.
 */
@Composable
fun ChildToParentsScreen(
    allPals: List<Pal>,
    childQuery: Pal?,
    parentPairs: List<Pair<Pal, Pal>>,
    onChildSelected: (Pal) -> Unit,
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
            text = "Escolha o Pal que você quer obter",
            style = MaterialTheme.typography.titleMedium,
            color = HudTextLight
        )

        PalPickerField(
            label = "Pal desejado",
            allPals = allPals,
            selected = childQuery,
            onPalSelected = onChildSelected
        )

        if (childQuery != null) {
            Text(
                text = "${parentPairs.size} combinação(ões) encontrada(s)",
                style = MaterialTheme.typography.labelLarge,
                color = HudTextMuted
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = parentPairs, key = { "${it.first.id}-${it.second.id}" }) { pair ->
                    GameWindowPanel(accentColor = HudCyanAccent, cornerRadius = 14.dp, contentPadding = 12.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PalIcon(pal = pair.first, size = 40.dp)
                                Spacer(modifier = Modifier.size(4.dp))
                                Text("+", color = HudGoldAccent, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.size(4.dp))
                                PalIcon(pal = pair.second, size = 40.dp)
                            }
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "${pair.first.displayName} + ${pair.second.displayName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = HudTextLight
                            )
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
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

private val previewRelaxaurus = Pal(
    "relaxaurus", "Relaxaurus", 280, 279,
    iconUrl = "https://palworld.wiki.gg/images/thumb/Relaxaurus_icon.png/128px-Relaxaurus_icon.png?3b6339"
)
private val previewSparkit = Pal(
    "sparkit", "Sparkit", 1410, 291,
    iconUrl = "https://palworld.wiki.gg/images/thumb/Sparkit_icon.png/128px-Sparkit_icon.png?14cd2f"
)
private val previewRelaxaurusLux = Pal(
    "relaxaurus_lux", "Relaxaurus Lux", 270, 280, eligibleAsFormulaChild = false,
    iconUrl = "https://palworld.wiki.gg/images/thumb/Relaxaurus_Lux_icon.png/128px-Relaxaurus_Lux_icon.png?56d4bf"
)
private val previewPals = listOf(previewRelaxaurus, previewSparkit, previewRelaxaurusLux)

@Preview(showBackground = true, name = "Sem seleção")
@Composable
private fun ChildToParentsScreenEmptyPreview() {
    PalHelperTheme {
        ChildToParentsScreen(
            allPals = previewPals,
            childQuery = null,
            parentPairs = emptyList(),
            onChildSelected = {},
            onClear = {}
        )
    }
}

@Preview(showBackground = true, name = "Com resultados")
@Composable
private fun ChildToParentsScreenResultsPreview() {
    PalHelperTheme {
        ChildToParentsScreen(
            allPals = previewPals,
            childQuery = previewRelaxaurusLux,
            parentPairs = listOf(previewRelaxaurus to previewSparkit),
            onChildSelected = {},
            onClear = {}
        )
    }
}
