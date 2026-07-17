package com.palhelper.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.palhelper.app.ui.screens.ChildToParentsScreen
import com.palhelper.app.ui.screens.TwoPalsToChildScreen
import com.palhelper.app.ui.theme.HudGoldAccent
import com.palhelper.app.ui.theme.HudNavyDark
import com.palhelper.app.ui.theme.HudNavyMid
import com.palhelper.app.ui.theme.HudTextLight
import com.palhelper.app.ui.theme.PalHelperTheme

private val TAB_TITLES = listOf("2 Pals → Filho", "Filho → Pais")

/**
 * Breeding tool root: a two-tab layout switching between the two breeding modes, styled like
 * Palworld's blue in-game HUD windows. [onBack] returns to the home menu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedingApp(
    onBack: () -> Unit,
    viewModel: BreedingViewModel = remember { BreedingViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🥚 Calculadora de Breeding",
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
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = HudNavyMid,
                contentColor = HudGoldAccent
            ) {
                TAB_TITLES.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, color = if (selectedTab == index) HudGoldAccent else HudTextLight) }
                    )
                }
            }

            when (selectedTab) {
                0 -> TwoPalsToChildScreen(
                    allPals = uiState.allPals,
                    parentA = uiState.parentA,
                    parentB = uiState.parentB,
                    computedChild = uiState.computedChild,
                    onParentASelected = viewModel::selectParentA,
                    onParentBSelected = viewModel::selectParentB,
                    onClear = viewModel::clearParents
                )
                else -> ChildToParentsScreen(
                    allPals = uiState.allPals,
                    childQuery = uiState.childQuery,
                    parentPairs = uiState.parentPairs,
                    onChildSelected = viewModel::selectChildQuery,
                    onClear = viewModel::clearChildQuery
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "BreedingApp")
@Composable
private fun BreedingAppPreview() {
    PalHelperTheme {
        BreedingApp(onBack = {})
    }
}
