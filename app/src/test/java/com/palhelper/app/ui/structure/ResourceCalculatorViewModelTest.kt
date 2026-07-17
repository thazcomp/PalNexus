package com.palhelper.app.ui.structure

import app.cash.turbine.test
import com.palhelper.app.data.StructureRepository
import com.palhelper.app.data.model.structure.MaterialCost
import com.palhelper.app.data.model.structure.Structure
import com.palhelper.app.data.model.structure.StructureCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * TDD spec for [ResourceCalculatorViewModel]: adding/removing structures updates the selection
 * and the running material total, exposed as a single [kotlinx.coroutines.flow.StateFlow].
 */
class ResourceCalculatorViewModelTest {

    private val woodenChest = Structure(
        id = "wooden_chest", displayName = "Wooden Chest", category = StructureCategory.STORAGE,
        techLevel = 3, costs = listOf(MaterialCost("wood", "Wood", 20))
    )
    private val palbox = Structure(
        id = "palbox", displayName = "Palbox", category = StructureCategory.PALS, techLevel = null,
        costs = listOf(MaterialCost("wood", "Wood", 50), MaterialCost("stone", "Stone", 20))
    )

    private fun buildViewModel() =
        ResourceCalculatorViewModel(StructureRepository(initialStructures = listOf(woodenChest, palbox)))

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state lists all structures with an empty selection`() = runTest {
        val viewModel = buildViewModel()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(listOf(woodenChest, palbox), state.allStructures)
            assertTrue(state.selection.isEmpty())
            assertTrue(state.totalMaterials.isEmpty())
        }
    }

    @Test
    fun `adding a structure increments its quantity and updates totals`() = runTest {
        val viewModel = buildViewModel()
        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.add(woodenChest)
            val state = awaitItem()
            assertEquals(1, state.selection[woodenChest])
            assertEquals(20, state.totalMaterials.first { it.materialId == "wood" }.amount)
        }
    }

    @Test
    fun `adding the same structure twice sums the totals`() = runTest {
        val viewModel = buildViewModel()
        viewModel.uiState.test {
            awaitItem()
            viewModel.add(woodenChest)
            awaitItem()
            viewModel.add(woodenChest)
            val state = awaitItem()
            assertEquals(2, state.selection[woodenChest])
            assertEquals(40, state.totalMaterials.first { it.materialId == "wood" }.amount)
        }
    }

    @Test
    fun `removing decrements and drops the structure at zero`() = runTest {
        val viewModel = buildViewModel()
        viewModel.uiState.test {
            awaitItem()
            viewModel.add(woodenChest)
            awaitItem()
            viewModel.remove(woodenChest)
            val state = awaitItem()
            assertTrue(state.selection.isEmpty())
            assertTrue(state.totalMaterials.isEmpty())
        }
    }

    @Test
    fun `totals combine shared materials across different structures`() = runTest {
        val viewModel = buildViewModel()
        viewModel.uiState.test {
            awaitItem()
            viewModel.add(woodenChest) // 20 wood
            awaitItem()
            viewModel.add(palbox)      // 50 wood, 20 stone
            val state = awaitItem()
            val byId = state.totalMaterials.associateBy { it.materialId }
            assertEquals(70, byId.getValue("wood").amount)
            assertEquals(20, byId.getValue("stone").amount)
        }
    }

    @Test
    fun `clear empties the whole selection`() = runTest {
        val viewModel = buildViewModel()
        viewModel.uiState.test {
            awaitItem()
            viewModel.add(woodenChest)
            awaitItem()
            viewModel.add(palbox)
            awaitItem()
            viewModel.clear()
            val state = awaitItem()
            assertTrue(state.selection.isEmpty())
            assertTrue(state.totalMaterials.isEmpty())
        }
    }
}
