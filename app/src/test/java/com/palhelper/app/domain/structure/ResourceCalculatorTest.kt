package com.palhelper.app.domain.structure

import com.palhelper.app.data.model.structure.MaterialCost
import com.palhelper.app.data.model.structure.Structure
import com.palhelper.app.data.model.structure.StructureCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * TDD spec for [ResourceCalculator], which sums up the materials needed to build a chosen set
 * of structures (with quantities), so the user can plan a whole base build at once.
 */
class ResourceCalculatorTest {

    private fun cost(id: String, amount: Int) =
        MaterialCost(materialId = id, displayName = id.replaceFirstChar { it.uppercase() }, amount = amount)

    private val woodenChest = Structure(
        id = "wooden_chest",
        displayName = "Wooden Chest",
        category = StructureCategory.STORAGE,
        techLevel = 3,
        costs = listOf(cost("wood", 20))
    )

    private val palbox = Structure(
        id = "palbox",
        displayName = "Palbox",
        category = StructureCategory.PALS,
        techLevel = null,
        costs = listOf(cost("paldium_fragment", 10), cost("wood", 50), cost("stone", 20))
    )

    private val calculator = ResourceCalculator()

    @Test
    fun `empty selection totals to nothing`() {
        assertTrue(calculator.totalMaterials(emptyMap()).isEmpty())
    }

    @Test
    fun `single structure with quantity one returns its own costs`() {
        val totals = calculator.totalMaterials(mapOf(woodenChest to 1))
        assertEquals(1, totals.size)
        assertEquals(20, totals.first { it.materialId == "wood" }.amount)
    }

    @Test
    fun `quantity multiplies each material`() {
        val totals = calculator.totalMaterials(mapOf(woodenChest to 3))
        assertEquals(60, totals.first { it.materialId == "wood" }.amount)
    }

    @Test
    fun `materials shared across structures are summed together`() {
        // wooden_chest (20 wood) x2 + palbox (50 wood, 20 stone, 10 paldium) x1
        val totals = calculator.totalMaterials(mapOf(woodenChest to 2, palbox to 1))
        val byId = totals.associateBy { it.materialId }
        assertEquals(90, byId.getValue("wood").amount)      // 40 + 50
        assertEquals(20, byId.getValue("stone").amount)
        assertEquals(10, byId.getValue("paldium_fragment").amount)
        assertEquals(3, byId.size)
    }

    @Test
    fun `structures with quantity zero are ignored`() {
        val totals = calculator.totalMaterials(mapOf(woodenChest to 0, palbox to 1))
        val byId = totals.associateBy { it.materialId }
        assertEquals(50, byId.getValue("wood").amount)
        assertTrue(byId.containsKey("paldium_fragment"))
    }

    @Test
    fun `result is sorted by descending amount for stable, useful display`() {
        val totals = calculator.totalMaterials(mapOf(palbox to 1))
        val amounts = totals.map { it.amount }
        assertEquals(amounts.sortedDescending(), amounts)
    }

    @Test
    fun `material display name and icon are preserved from the first occurrence`() {
        val iconChest = woodenChest.copy(
            costs = listOf(MaterialCost("wood", "Wood", 20, iconUrl = "http://example/wood.png"))
        )
        val totals = calculator.totalMaterials(mapOf(iconChest to 1))
        val wood = totals.first { it.materialId == "wood" }
        assertEquals("Wood", wood.displayName)
        assertEquals("http://example/wood.png", wood.iconUrl)
    }
}
