package com.palhelper.app.ui

import app.cash.turbine.test
import com.palhelper.app.data.PalRepository
import com.palhelper.app.data.model.Pal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * TDD spec for [BreedingViewModel], covering both modes the user asked for:
 *  - Pick two Pals -> see the resulting child.
 *  - Pick one Pal (the child) -> see every parent pair that can produce it.
 */
class BreedingViewModelTest {

    private val pal10 = Pal(id = "pal10", displayName = "Pal10", breedingPower = 100, paldeckIndex = 5)
    private val pal20 = Pal(id = "pal20", displayName = "Pal20", breedingPower = 200, paldeckIndex = 3)
    private val pal30 = Pal(id = "pal30", displayName = "Pal30", breedingPower = 300, paldeckIndex = 8)

    private val fixturePals = listOf(pal10, pal20, pal30)

    private fun buildViewModel(): BreedingViewModel {
        val repository = PalRepository(
            initialPals = fixturePals,
            specialCombinations = emptyMap(),
            sameSpeciesOnlyIds = emptySet()
        )
        return BreedingViewModel(repository)
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state exposes the full pal roster and no selection`() = runTest {
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(fixturePals, state.allPals)
            assertNull(state.parentA)
            assertNull(state.parentB)
            assertNull(state.computedChild)
        }
    }

    @Test
    fun `selecting both parents computes the resulting child`() = runTest {
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            awaitItem() // initial state

            viewModel.selectParentA(pal10)
            awaitItem() // parentA set, computedChild still null (parentB missing)

            viewModel.selectParentB(pal30)
            val stateWithChild = awaitItem()

            assertEquals(pal10, stateWithChild.parentA)
            assertEquals(pal30, stateWithChild.parentB)
            // (100 + 300 + 1) / 2 == 200 -> pal20
            assertEquals(pal20, stateWithChild.computedChild)
        }
    }

    @Test
    fun `clearing parents resets the computed child`() = runTest {
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.selectParentA(pal10)
            awaitItem()
            viewModel.selectParentB(pal30)
            awaitItem()

            viewModel.clearParents()
            val cleared = awaitItem()

            assertNull(cleared.parentA)
            assertNull(cleared.parentB)
            assertNull(cleared.computedChild)
        }
    }

    @Test
    fun `selecting a child query populates its parent pairs`() = runTest {
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            awaitItem() // initial

            viewModel.selectChildQuery(pal20)
            val state = awaitItem()

            assertEquals(pal20, state.childQuery)
            // pal10 + pal30 averages exactly to pal20's rank
            assertTrue(state.parentPairs.any { setOf(it.first.id, it.second.id) == setOf("pal10", "pal30") })
        }
    }

    @Test
    fun `clearing the child query empties parent pairs`() = runTest {
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.selectChildQuery(pal20)
            awaitItem()

            viewModel.clearChildQuery()
            val cleared = awaitItem()

            assertNull(cleared.childQuery)
            assertTrue(cleared.parentPairs.isEmpty())
        }
    }
}
