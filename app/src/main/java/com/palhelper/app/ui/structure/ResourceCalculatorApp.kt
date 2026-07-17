package com.palhelper.app.ui.structure

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.palhelper.app.ui.theme.HudNavyDark
import com.palhelper.app.ui.theme.HudNavyMid
import com.palhelper.app.ui.theme.HudTextLight
import com.palhelper.app.ui.theme.PalHelperTheme

/**
 * Screen-level wrapper for the resource calculator: adds the HUD top bar (with a back button
 * to the home menu) and wires the [ResourceCalculatorViewModel] to [ResourceCalculatorScreen].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceCalculatorApp(
    onBack: () -> Unit,
    viewModel: ResourceCalculatorViewModel = remember { ResourceCalculatorViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🏗️ Calculadora de Recursos",
                        fontWeight = FontWeight.Bold,
                        color = HudTextLight
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = HudTextLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HudNavyMid)
            )
        },
        containerColor = HudNavyDark
    ) { paddingValues ->
        ResourceCalculatorScreen(
            allStructures = uiState.allStructures,
            selection = uiState.selection,
            totalMaterials = uiState.totalMaterials,
            onAdd = viewModel::add,
            onRemove = viewModel::remove,
            onClear = viewModel::clear,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Preview(showBackground = true, name = "ResourceCalculatorApp")
@Composable
private fun ResourceCalculatorAppPreview() {
    PalHelperTheme {
        ResourceCalculatorApp(onBack = {})
    }
}
