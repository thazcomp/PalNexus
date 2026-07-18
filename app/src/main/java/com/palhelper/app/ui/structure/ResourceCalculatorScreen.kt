package com.palhelper.app.ui.structure

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.palhelper.app.R
import com.palhelper.app.data.model.structure.MaterialCost
import com.palhelper.app.data.model.structure.Structure
import com.palhelper.app.data.model.structure.StructureCategory
import com.palhelper.app.data.model.structure.getPortugueseName
import com.palhelper.app.ui.components.GameWindowPanel
import com.palhelper.app.ui.components.RemoteIcon
import com.palhelper.app.ui.theme.HudCyanAccent
import com.palhelper.app.ui.theme.HudGoldAccent
import com.palhelper.app.ui.theme.HudNavyDark
import com.palhelper.app.ui.theme.HudNavyMid
import com.palhelper.app.ui.theme.HudTextLight
import com.palhelper.app.ui.theme.HudTextMuted
import com.palhelper.app.ui.theme.PalHelperTheme

/** How many structures are shown per page in the grid. */
private const val PAGE_SIZE = 24

/**
 * The resource calculator screen: a grid of base structures with icons. Tapping a card opens a
 * detail dialog showing that structure's material cost and lets the user add it to the build
 * list. A running total of all materials needed (summed across the whole selection) sits at the
 * bottom in a HUD panel.
 *
 * Because the full 1.0 structure list is large, the grid is paginated: an optional category
 * filter narrows the list, and the results are shown [PAGE_SIZE] at a time with prev/next
 * controls, so only one page of cards renders (and loads its icons) at once.
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
    var selectedCategory by remember { mutableStateOf<StructureCategory?>(null) }
    var page by remember { mutableIntStateOf(0) }

    // Categories that actually have entries, in enum order, for the filter chips.
    val availableCategories = remember(allStructures) {
        StructureCategory.entries.filter { cat -> allStructures.any { it.category == cat } }
    }

    val filtered = remember(allStructures, selectedCategory) {
        if (selectedCategory == null) allStructures
        else allStructures.filter { it.category == selectedCategory }
    }

    val pageCount = if (filtered.isEmpty()) 1 else (filtered.size + PAGE_SIZE - 1) / PAGE_SIZE
    // Keep the current page in range whenever the filter changes.
    val safePage = page.coerceIn(0, pageCount - 1)
    if (safePage != page) page = safePage

    val pageItems = remember(filtered, safePage) {
        val from = safePage * PAGE_SIZE
        val to = minOf(from + PAGE_SIZE, filtered.size)
        if (from < to) filtered.subList(from, to) else emptyList()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HudNavyDark)
    ) {
        Text(
            text = stringResource(R.string.calculator_instruction),
            style = MaterialTheme.typography.bodyMedium,
            color = HudTextMuted,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        CategoryFilterRow(
            categories = availableCategories,
            selected = selectedCategory,
            onSelect = {
                selectedCategory = it
                page = 0
            }
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 104.dp),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items = pageItems, key = { it.id }) { structure ->
                StructureCard(
                    structure = structure,
                    quantity = selection[structure] ?: 0,
                    onClick = { detailStructure = structure }
                )
            }
        }

        PaginationBar(
            page = safePage,
            pageCount = pageCount,
            totalItems = filtered.size,
            onPrev = { if (safePage > 0) page = safePage - 1 },
            onNext = { if (safePage < pageCount - 1) page = safePage + 1 }
        )

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
private fun CategoryFilterRow(
    categories: List<StructureCategory>,
    selected: StructureCategory?,
    onSelect: (StructureCategory?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(label = stringResource(R.string.filter_all), selected = selected == null, onClick = { onSelect(null) })
        categories.forEach { category ->
            FilterChip(
                label = stringResource(category.getDisplayNameRes()),
                selected = selected == category,
                onClick = { onSelect(category) }
            )
        }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) HudGoldAccent else HudNavyMid
    val fg = if (selected) HudNavyDark else HudTextLight
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = fg,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun PaginationBar(
    page: Int,
    pageCount: Int,
    totalItems: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onPrev,
            enabled = page > 0,
            colors = ButtonDefaults.buttonColors(
                containerColor = HudNavyMid,
                disabledContainerColor = HudNavyMid.copy(alpha = 0.4f)
            )
        ) {
            Text("‹ " + stringResource(R.string.pagination_previous), color = if (page > 0) HudTextLight else HudTextMuted)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.pagination_page, page + 1, pageCount),
                style = MaterialTheme.typography.labelLarge,
                color = HudCyanAccent,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.pagination_total_structures, totalItems),
                style = MaterialTheme.typography.labelSmall,
                color = HudTextMuted
            )
        }

        Button(
            onClick = onNext,
            enabled = page < pageCount - 1,
            colors = ButtonDefaults.buttonColors(
                containerColor = HudNavyMid,
                disabledContainerColor = HudNavyMid.copy(alpha = 0.4f)
            )
        ) {
            Text(stringResource(R.string.pagination_next) + " ›", color = if (page < pageCount - 1) HudTextLight else HudTextMuted)
        }
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
                    fallbackLabel = structure.getPortugueseName(),
                    contentDescription = structure.getPortugueseName(),
                    size = 52.dp
                )
                Text(
                    text = structure.getPortugueseName(),
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
                    text = stringResource(R.string.total_resources, structureCount),
                    style = MaterialTheme.typography.titleMedium,
                    color = HudGoldAccent,
                    fontWeight = FontWeight.Bold
                )
                if (structureCount > 0) {
                    TextButton(onClick = onClear) {
                        Text(stringResource(R.string.clear_button), color = HudTextMuted)
                    }
                }
            }

            if (totalMaterials.isEmpty()) {
                Text(
                    text = stringResource(R.string.empty_resources_message),
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
                            fallbackLabel = material.getPortugueseName(),
                            contentDescription = material.getPortugueseName(),
                            size = 34.dp,
                            accentColor = HudGoldAccent
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = material.getPortugueseName(),
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
                        fallbackLabel = structure.getPortugueseName(),
                        contentDescription = structure.getPortugueseName(),
                        size = 56.dp
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Column {
                        Text(
                            text = structure.getPortugueseName(),
                            style = MaterialTheme.typography.titleMedium,
                            color = HudTextLight,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(structure.category.getDisplayNameRes()) +
                                (structure.techLevel?.let { " • ${stringResource(R.string.tech_level, it)}" } ?: ""),
                            style = MaterialTheme.typography.bodyMedium,
                            color = HudTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.structure_cost_title),
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
                            fallbackLabel = material.getPortugueseName(),
                            contentDescription = material.getPortugueseName(),
                            size = 34.dp
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = material.getPortugueseName(),
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
                        Text(stringResource(R.string.close_button), color = HudCyanAccent)
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

/** A larger fixture so the pagination controls have more than one page in the preview. */
private val previewManyStructures = buildList {
    add(previewPalbox)
    add(previewChest)
    repeat(30) { i ->
        add(
            Structure(
                "wall_$i", "Wooden Wall $i", StructureCategory.FOUNDATION, 2,
                listOf(MaterialCost("wood", "Wood", 2)),
                iconUrl = "https://palworld.gg/images/items/T_icon_buildObject_Wood_wall.png"
            )
        )
    }
}

@Preview(showBackground = true, name = "Calculadora - vazia")
@Composable
private fun ResourceCalculatorScreenEmptyPreview() {
    PalHelperTheme {
        ResourceCalculatorScreen(
            allStructures = previewManyStructures,
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
            allStructures = previewManyStructures,
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

@Preview(showBackground = true, name = "Barra de paginação")
@Composable
private fun PaginationBarPreview() {
    PalHelperTheme {
        Box(modifier = Modifier.background(HudNavyDark)) {
            PaginationBar(page = 1, pageCount = 6, totalItems = 124, onPrev = {}, onNext = {})
        }
    }
}
