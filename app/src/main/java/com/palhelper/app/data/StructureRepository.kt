package com.palhelper.app.data

import com.palhelper.app.data.model.structure.Structure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Exposes the buildable-structure roster as a [StateFlow], mirroring [PalRepository]. The real
 * dataset ([StructureDataSource]) is the default, but it's a constructor parameter so tests can
 * substitute a small fixture.
 */
class StructureRepository(
    initialStructures: List<Structure> = StructureDataSource.allStructures
) {
    private val _structures: MutableStateFlow<List<Structure>> = MutableStateFlow(initialStructures)
    val structures: StateFlow<List<Structure>> = _structures.asStateFlow()

    fun findById(id: String): Structure? = _structures.value.find { it.id == id }
}
