package com.palhelper.app.ui.structure

import androidx.lifecycle.ViewModel
import com.palhelper.app.data.StructureRepository
import com.palhelper.app.data.model.structure.MaterialCost
import com.palhelper.app.data.model.structure.Structure
import com.palhelper.app.domain.structure.ResourceCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI state for the resource calculator: the full structure roster, the current selection
 * (structure -> quantity), and the running total of all materials needed.
 */
data class ResourceCalculatorUiState(
    val allStructures: List<Structure> = emptyList(),
    val selection: Map<Structure, Int> = emptyMap(),
    val totalMaterials: List<MaterialCost> = emptyList()
)

class ResourceCalculatorViewModel(
    private val repository: StructureRepository = StructureRepository()
) : ViewModel() {

    private val calculator = ResourceCalculator()

    private val _uiState = MutableStateFlow(
        ResourceCalculatorUiState(allStructures = repository.structures.value)
    )
    val uiState: StateFlow<ResourceCalculatorUiState> = _uiState.asStateFlow()

    fun add(structure: Structure) {
        _uiState.update { state ->
            val newSelection = state.selection.toMutableMap()
            newSelection[structure] = (newSelection[structure] ?: 0) + 1
            state.copy(
                selection = newSelection,
                totalMaterials = calculator.totalMaterials(newSelection)
            )
        }
    }

    fun remove(structure: Structure) {
        _uiState.update { state ->
            val current = state.selection[structure] ?: return@update state
            val newSelection = state.selection.toMutableMap()
            if (current <= 1) newSelection.remove(structure) else newSelection[structure] = current - 1
            state.copy(
                selection = newSelection,
                totalMaterials = calculator.totalMaterials(newSelection)
            )
        }
    }

    fun clear() {
        _uiState.update { it.copy(selection = emptyMap(), totalMaterials = emptyList()) }
    }
}
