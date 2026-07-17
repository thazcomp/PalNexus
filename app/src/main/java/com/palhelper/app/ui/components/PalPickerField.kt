package com.palhelper.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.palhelper.app.data.model.Pal
import com.palhelper.app.ui.theme.HudCyanAccent
import com.palhelper.app.ui.theme.HudGoldAccent
import com.palhelper.app.ui.theme.HudNavyPanel
import com.palhelper.app.ui.theme.HudTextLight
import com.palhelper.app.ui.theme.HudTextMuted
import com.palhelper.app.ui.theme.PalHelperTheme

/**
 * A tappable, HUD-styled field that opens a searchable list of every known Pal, showing
 * each Pal's icon (see [PalIcon]). Used for both breeding modes: picking parents, and picking
 * the child to reverse-search.
 */
@Composable
fun PalPickerField(
    label: String,
    allPals: List<Pal>,
    selected: Pal?,
    onPalSelected: (Pal) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    GameWindowPanel(
        modifier = modifier
            .padding(vertical = 4.dp)
            .clickable { showDialog = true },
        accentColor = if (selected != null) HudGoldAccent else HudCyanAccent,
        cornerRadius = 14.dp,
        contentPadding = 12.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (selected != null) {
                PalIcon(pal = selected, size = 44.dp)
                Spacer(modifier = Modifier.size(12.dp))
            }
            Column {
                Text(text = label, style = MaterialTheme.typography.labelLarge, color = HudTextMuted)
                Text(
                    text = selected?.displayName ?: "Toque para escolher um Pal",
                    style = MaterialTheme.typography.bodyLarge,
                    color = HudTextLight
                )
            }
        }
    }

    if (showDialog) {
        PalSearchDialog(
            title = label,
            allPals = allPals,
            onDismiss = { showDialog = false },
            onPalSelected = {
                onPalSelected(it)
                showDialog = false
            }
        )
    }
}

@Composable
private fun PalSearchDialog(
    title: String,
    allPals: List<Pal>,
    onDismiss: () -> Unit,
    onPalSelected: (Pal) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, allPals) {
        if (query.isBlank()) {
            allPals
        } else {
            allPals.filter { it.displayName.contains(query, ignoreCase = true) }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        GameWindowPanel(accentColor = HudGoldAccent, cornerRadius = 20.dp) {
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = HudTextLight)
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Buscar Pal") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                LazyColumn(modifier = Modifier.heightIn(max = 420.dp)) {
                    items(items = filtered, key = { it.id }) { pal ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = { onPalSelected(pal) })
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PalIcon(pal = pal, size = 40.dp)
                            Spacer(modifier = Modifier.size(12.dp))
                            Text(pal.displayName, color = HudTextLight)
                        }
                        HorizontalDivider(color = HudNavyPanel)
                    }
                }
            }
        }
    }
}

private val previewPals = listOf(
    Pal(
        "lamball", "Lamball", 1470, 252,
        iconUrl = "https://palworld.wiki.gg/images/thumb/Lamball_icon.png/128px-Lamball_icon.png?235903"
    ),
    Pal(
        "cattiva", "Cattiva", 1460, 271,
        iconUrl = "https://palworld.wiki.gg/images/thumb/Cattiva_icon.png/128px-Cattiva_icon.png?31e506"
    ),
    Pal("relaxaurus", "Relaxaurus", 280, 279)
)

@Preview(showBackground = true, backgroundColor = 0xFF071B33, name = "PalPickerField - vazio")
@Composable
private fun PalPickerFieldEmptyPreview() {
    PalHelperTheme {
        PalPickerField(
            label = "Pal 1 (pai/mãe A)",
            allPals = previewPals,
            selected = null,
            onPalSelected = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF071B33, name = "PalPickerField - selecionado")
@Composable
private fun PalPickerFieldSelectedPreview() {
    PalHelperTheme {
        PalPickerField(
            label = "Pal 1 (pai/mãe A)",
            allPals = previewPals,
            selected = previewPals.first(),
            onPalSelected = {}
        )
    }
}
