package com.palhelper.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.palhelper.app.ui.screens.PalIconLoadingScreen
import com.palhelper.app.ui.structure.ResourceCalculatorApp
import com.palhelper.app.ui.theme.PalHelperTheme

/**
 * App entry point and navigation host. Shows the [HomeMenuScreen] first, then routes to either
 * the breeding tool or the resource calculator based on the user's choice; the system back button
 * (and the top-bar back arrow) returns to the menu.
 *
 * Pal icons preload in the background (see [preloadPalIcons]) while the menu is already usable —
 * by the time the user opens the breeding tool the icons are typically cached, but the resource
 * calculator (which doesn't need Pal icons) is never blocked by it.
 */
@Composable
fun PalHelperRoot(
    breedingViewModel: BreedingViewModel = remember { BreedingViewModel() }
) {
    val context = LocalContext.current
    var destination by remember { mutableStateOf<PalHelperDestination?>(null) }

    // Kick off Pal-icon preloading once, in the background; the UI is usable meanwhile.
    var preloadLoaded by remember { mutableIntStateOf(0) }
    var preloadTotal by remember { mutableIntStateOf(0) }
    var preloadDone by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val iconUrls = breedingViewModel.uiState.value.allPals.mapNotNull { it.iconUrl }
        preloadPalIcons(context = context, urls = iconUrls) { loaded, total ->
            preloadLoaded = loaded
            preloadTotal = total
        }
        preloadDone = true
    }

    when (destination) {
        null -> HomeMenuScreen(onSelect = { destination = it })

        PalHelperDestination.BREEDING -> {
            BackHandler { destination = null }
            // If the user opens breeding before icons finish, show the loader briefly so the
            // picker doesn't pop icons in one by one; otherwise go straight in.
            if (!preloadDone) {
                PalIconLoadingScreen(loaded = preloadLoaded, total = preloadTotal)
            } else {
                BreedingApp(onBack = { destination = null }, viewModel = breedingViewModel)
            }
        }

        PalHelperDestination.RESOURCES -> {
            BackHandler { destination = null }
            ResourceCalculatorApp(onBack = { destination = null })
        }
    }
}

@Preview(showBackground = true, name = "PalHelperRoot (menu)")
@Composable
private fun PalHelperRootPreview() {
    PalHelperTheme {
        HomeMenuScreen(onSelect = {})
    }
}
