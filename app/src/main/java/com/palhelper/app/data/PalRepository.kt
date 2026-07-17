package com.palhelper.app.data

import com.palhelper.app.data.model.Pal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Exposes the Pal roster as a [StateFlow] so the UI layer can observe it reactively.
 * The dataset is static today, but this shape lets it later be swapped for something
 * loaded asynchronously (e.g. a bundled JSON asset or a remote source) without touching
 * call sites, since they only ever see a [StateFlow] of [Pal].
 *
 * The real dataset ([PalDataSource]) is used by default, but every dependency is a
 * constructor parameter so tests can substitute a small fixture instead.
 */
class PalRepository(
    initialPals: List<Pal> = PalDataSource.allPals,
    val specialCombinations: Map<Set<String>, String> = PalDataSource.specialCombinations,
    val sameSpeciesOnlyIds: Set<String> = PalDataSource.sameSpeciesOnlyIds
) {
    private val _pals: MutableStateFlow<List<Pal>> = MutableStateFlow(initialPals)
    val pals: StateFlow<List<Pal>> = _pals.asStateFlow()

    fun findById(id: String): Pal? = _pals.value.find { it.id == id }
}
