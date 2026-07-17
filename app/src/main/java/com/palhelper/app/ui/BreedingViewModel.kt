package com.palhelper.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palhelper.app.data.PalRepository
import com.palhelper.app.data.model.Pal
import com.palhelper.app.domain.BreedingCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for both breeding modes:
 *  - "Two Pals -> child": fill [parentA] and [parentB], read [computedChild].
 *  - "One Pal -> parents": fill [childQuery], read [parentPairs].
 */
data class BreedingUiState(
    val allPals: List<Pal> = emptyList(),
    val parentA: Pal? = null,
    val parentB: Pal? = null,
    val computedChild: Pal? = null,
    val childQuery: Pal? = null,
    val parentPairs: List<Pair<Pal, Pal>> = emptyList()
)

class BreedingViewModel(
    private val repository: PalRepository = PalRepository()
) : ViewModel() {

    private val calculator = BreedingCalculator(
        pals = repository.pals.value,
        specialCombinations = repository.specialCombinations,
        sameSpeciesOnlyIds = repository.sameSpeciesOnlyIds
    )

    private val _uiState = MutableStateFlow(BreedingUiState(allPals = repository.pals.value))
    val uiState: StateFlow<BreedingUiState> = _uiState.asStateFlow()

    fun selectParentA(pal: Pal) {
        _uiState.update { it.copy(parentA = pal) }
        recomputeChild()
    }

    fun selectParentB(pal: Pal) {
        _uiState.update { it.copy(parentB = pal) }
        recomputeChild()
    }

    fun clearParents() {
        _uiState.update { it.copy(parentA = null, parentB = null, computedChild = null) }
    }

    fun selectChildQuery(pal: Pal) {
        _uiState.update { it.copy(childQuery = pal) }
        recomputeParentPairs()
    }

    fun clearChildQuery() {
        _uiState.update { it.copy(childQuery = null, parentPairs = emptyList()) }
    }

    private fun recomputeChild() {
        val state = _uiState.value
        val parentA = state.parentA
        val parentB = state.parentB
        if (parentA == null || parentB == null) {
            _uiState.update { it.copy(computedChild = null) }
            return
        }

        viewModelScope.launch {
            val child = calculator.calculateChild(parentA, parentB)
            _uiState.update { it.copy(computedChild = child) }
        }
    }

    private fun recomputeParentPairs() {
        val child = _uiState.value.childQuery ?: return

        viewModelScope.launch {
            val pairs = calculator.findParentPairs(child)
            _uiState.update { it.copy(parentPairs = pairs) }
        }
    }
}
