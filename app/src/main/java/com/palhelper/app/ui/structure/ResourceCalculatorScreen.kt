package com.palhelper.app.ui.structure

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.palhelper.app.data.model.structure.MaterialCost
import com.palhelper.app.data.model.structure.Structure
import com.palhelper.app.data.model.structure.StructureCategory
import com.palhelper.app.ui.components.GameWindowPanel
import com.palhelper.app.ui.components.RemoteIcon
import com.palhelper.app.ui.theme.HudCyanAccent
import com.palhelper.app.ui.theme.HudGoldAccent
import com.palhelper.app.ui.theme.HudNavyDark
import com.palhelper.app.ui.theme.HudNavyMid
import com.palhelper.app.ui.theme.HudTextLight
import com.palhelper.app.ui.theme.HudTextMuted
import com.palhelper.app.ui.theme.PalHelperTheme

/**
 * The resource calculator screen: a grid of base structures with icons. Tapping a card opens a
 * detail dialog showing that structure's material cost and lets the user add it to the build
 * list. A running total of all materials needed (summed across the whole selection) sits at the
 * bottom in a HUD panel.
 */
@Composable
fun ResourceCalculatorScreen(
    allStructures: List<Structure>,
    selection: Map<Structure, Int>,
    totalMaterials: List<MaterialCost>,
    onAdd: (Structure) -> Unit,
    onRemove: (Structure) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    var detailStructure by remember { mutableStateOf<Structure?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HudNavyDark)
    ) {
        Text(
            text = "Toque em uma construção para ver o custo e adicionar",
            style = MaterialTheme.typography.bodyMedium,
            color = HudTextMuted,
            modifier = Modifier.padding(16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 104.dp),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items = allStructures, key = { it.id }) { structure ->
                StructureCard(
                    structure = structure,
                    quantity = selection[structure] ?: 0,
                    onClick = { detailStructure = structure }
                )
            }
        }

        TotalMaterialsPanel(
            totalMaterials = totalMaterials,
            structureCount = selection.values.sum(),
            onClear = onClear
        )
    }

    detailStructure?.let { structure ->
        StructureDetailDialog(
            structure = structure,
            quantity = selection[structure] ?: 0,
            onAdd = { onAdd(structure) },
            onRemove = { onRemove(structure) },
            onDismiss = { detailStructure = null }
        )
    }
}

@Composable
private fun StructureCard(structure: Structure, quantity: Int, onClick: () -> Unit) {
    Box {
        GameWindowPanel(
            modifier = Modifier.clickable(onClick = onClick),
            accentColor = if (quantity > 0) HudGoldAccent else HudCyanAccent,
            cornerRadius = 12.dp,
            contentPadding = 8.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                RemoteIcon(
                    iconUrl = structure.iconUrl,
                    fallbackLabel = structure.displayName,
                    contentDescription = structure.displayName,
                    size = 52.dp
                )
                Text(
                    text = structure.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = HudTextLight,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }

        if (quantity > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .size(24.dp)
                    .background(HudGoldAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = HudNavyDark,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TotalMaterialsPanel(
    totalMaterials: List<MaterialCost>,
    structureCount: Int,
    onClear: () -> Unit
) {
    GameWindowPanel(
        modifier = Modifier.padding(12.dp),
        accentColor = HudGoldAccent,
        cornerRadius = 18.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total de recursos ($structureCount)",
                    style = MaterialTheme.typography.titleMedium,
                    color = HudGoldAccent,
                    fontWeight = FontWeight.Bold
                )
                if (structureCount > 0) {
                    TextButton(onClick = onClear) {
                        Text("Limpar", color = HudTextMuted)
                    }
                }
            }

            if (totalMaterials.isEmpty()) {
                Text(
                    text = "Adicione construções para ver os recursos somados aqui.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HudTextMuted,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                totalMaterials.forEach { material ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RemoteIcon(
                            iconUrl = material.iconUrl,
                            fallbackLabel = material.displayName,
                            contentDescription = material.displayName,
                            size = 34.dp,
                            accentColor = HudGoldAccent
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = material.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = HudTextLight,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "x${material.amount}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = HudGoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StructureDetailDialog(
    structure: Structure,
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        GameWindowPanel(accentColor = HudGoldAccent, cornerRadius = 20.dp) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RemoteIcon(
                        iconUrl = structure.iconUrl,
                        fallbackLabel = structure.displayName,
                        contentDescription = structure.displayName,
                        size = 56.dp
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Column {
                        Text(
                            text = structure.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = HudTextLight,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = structure.category.displayName +
                                (structure.techLevel?.let { " • Tecnologia Lv $it" } ?: ""),
                            style = MaterialTheme.typography.bodyMedium,
                            color = HudTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Custo de construção (por unidade)",
                    style = MaterialTheme.typography.labelLarge,
                    color = HudTextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))

                structure.costs.forEach { material ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RemoteIcon(
                            iconUrl = material.iconUrl,
                            fallbackLabel = material.displayName,
                            contentDescription = material.displayName,
                            size = 34.dp
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = material.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = HudTextLight,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "x${material.amount}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = HudCyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = onRemove,
                            enabled = quantity > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = HudNavyMid)
                        ) {
                            Text("–", color = HudTextLight, style = MaterialTheme.typography.titleMedium)
                        }
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = HudTextLight,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Button(
                            onClick = onAdd,
                            colors = ButtonDefaults.buttonColors(containerColor = HudGoldAccent)
                        ) {
                            Text("+", color = HudNavyDark, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Fechar", color = HudCyanAccent)
                    }
                }
            }
        }
    }
}

// --- Previews ---

private val previewWood = MaterialCost("wood", "Wood", 50, iconUrl = "https://palworld.gg/images/items/T_itemicon_Material_Wood.png")
private val previewStone = MaterialCost("stone", "Stone", 20, iconUrl = "https://palworld.gg/images/items/T_itemicon_Material_Stone.png")
private val previewPaldium = MaterialCost("paldium_fragment", "Paldium Fragment", 10)

private val previewPalbox = Structure(
    "palbox", "Palbox", StructureCategory.PALS, null,
    listOf(previewPaldium, previewWood, previewStone),
    iconUrl = "https://palworld.gg/images/items/T_icon_buildObject_PalControlPoint.png"
)
private val previewChest = Structure(
    "wooden_chest", "Wooden Chest", StructureCategory.STORAGE, 3,
    listOf(MaterialCost("wood", "Wood", 20)),
    iconUrl = "https://palworld.gg/images/items/T_icon_buildObject_ItemChest_01.png"
)
private val previewStructures = listOf(previewPalbox, previewChest)

@Preview(showBackground = true, name = "Calculadora - vazia")
@Composable
private fun ResourceCalculatorScreenEmptyPreview() {
    PalHelperTheme {
        ResourceCalculatorScreen(
            allStructures = previewStructures,
            selection = emptyMap(),
            totalMaterials = emptyList(),
            onAdd = {}, onRemove = {}, onClear = {}
        )
    }
}

@Preview(showBackground = true, name = "Calculadora - com seleção")
@Composable
private fun ResourceCalculatorScreenSelectedPreview() {
    PalHelperTheme {
        ResourceCalculatorScreen(
            allStructures = previewStructures,
            selection = mapOf(previewPalbox to 1, previewChest to 2),
            totalMaterials = listOf(
                MaterialCost("wood", "Wood", 90, iconUrl = previewWood.iconUrl),
                MaterialCost("stone", "Stone", 20, iconUrl = previewStone.iconUrl),
                MaterialCost("paldium_fragment", "Paldium Fragment", 10)
            ),
            onAdd = {}, onRemove = {}, onClear = {}
        )
    }
}
