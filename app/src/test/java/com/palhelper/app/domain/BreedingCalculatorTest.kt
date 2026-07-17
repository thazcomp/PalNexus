package com.palhelper.app.domain

import com.palhelper.app.data.PalDataSource
import com.palhelper.app.data.model.Pal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * TDD spec for [BreedingCalculator], written against Palworld's real (1.0) breeding mechanic:
 *
 *  1. Two Pals of the same species always produce that species.
 *  2. Some parent pairs are "special combos" that always override the formula.
 *  3. A handful of Pals can ONLY be bred from two parents of their own species.
 *  4. Otherwise: childRank = floor((rankA + rankB + 1) / 2), and the resulting Pal is whichever
 *     *formula-eligible* Pal has the rank closest to that value, ties broken by the lowest index.
 *
 * The first group of tests uses tiny hand-checkable fixtures. The final group runs against the
 * real [PalDataSource] to lock in known-correct 1.0 results (including user-verified ones).
 */
class BreedingCalculatorTest {

    private val pal10 = Pal(id = "pal10", displayName = "Pal10", breedingPower = 100, paldeckIndex = 5)
    private val pal20 = Pal(id = "pal20", displayName = "Pal20", breedingPower = 200, paldeckIndex = 3)
    private val pal30 = Pal(id = "pal30", displayName = "Pal30", breedingPower = 300, paldeckIndex = 8)
    private val pal199 = Pal(id = "pal199", displayName = "Pal199", breedingPower = 199, paldeckIndex = 50)
    private val ineligiblePal = Pal(
        id = "ineligible150",
        displayName = "Ineligible150",
        breedingPower = 150,
        paldeckIndex = 1,
        eligibleAsFormulaChild = false
    )

    private val relaxaurus = Pal(id = "relaxaurus", displayName = "Relaxaurus", breedingPower = 280, paldeckIndex = 279)
    private val sparkit = Pal(id = "sparkit", displayName = "Sparkit", breedingPower = 1410, paldeckIndex = 291)
    private val relaxaurusLux = Pal(
        id = "relaxaurus_lux",
        displayName = "Relaxaurus Lux",
        breedingPower = 270,
        paldeckIndex = 280,
        eligibleAsFormulaChild = false
    )

    private val jetragon = Pal(
        id = "jetragon",
        displayName = "Jetragon",
        breedingPower = 90,
        paldeckIndex = 333,
        eligibleAsFormulaChild = false
    )

    private val basicPals = listOf(pal10, pal20, pal30, pal199, ineligiblePal)
    private val specialComboPals = listOf(relaxaurus, sparkit, relaxaurusLux)

    @Test
    fun `same species pair always produces that species`() {
        val calculator = BreedingCalculator(pals = basicPals)
        assertEquals(pal10, calculator.calculateChild(pal10, pal10))
    }

    @Test
    fun `exact average match returns that pal`() {
        val calculator = BreedingCalculator(pals = basicPals)
        // (100 + 300 + 1) / 2 == 200 -> exact match on pal20
        assertEquals(pal20, calculator.calculateChild(pal10, pal30))
    }

    @Test
    fun `tie in distance is broken by the lowest paldeck index`() {
        val calculator = BreedingCalculator(pals = basicPals)
        // (100 + 200 + 1) / 2 == 150 -> equidistant between pal10 and pal20; pal20 index lower.
        assertEquals(pal20, calculator.calculateChild(pal10, pal20))
    }

    @Test
    fun `formula never resolves to a pal that is not eligible as a formula child`() {
        val calculator = BreedingCalculator(pals = basicPals)
        // (100 + 199 + 1) / 2 == 150 -> exact match on ineligiblePal, which must be skipped.
        assertEquals(pal10, calculator.calculateChild(pal10, pal199))
    }

    @Test
    fun `special combo overrides the standard formula`() {
        val calculator = BreedingCalculator(
            pals = specialComboPals,
            specialCombinations = mapOf(setOf("relaxaurus", "sparkit") to "relaxaurus_lux")
        )
        assertEquals(relaxaurusLux, calculator.calculateChild(relaxaurus, sparkit))
    }

    @Test
    fun `special combo works regardless of parent order`() {
        val calculator = BreedingCalculator(
            pals = specialComboPals,
            specialCombinations = mapOf(setOf("relaxaurus", "sparkit") to "relaxaurus_lux")
        )
        assertEquals(relaxaurusLux, calculator.calculateChild(sparkit, relaxaurus))
    }

    @Test
    fun `calculateChild returns null when a parent has no known breeding power`() {
        val unknownPowerPal = Pal(id = "unknown", displayName = "Unknown", breedingPower = null, paldeckIndex = null)
        val calculator = BreedingCalculator(pals = basicPals + unknownPowerPal)
        assertNull(calculator.calculateChild(pal10, unknownPowerPal))
    }

    @Test
    fun `findParentPairs includes the same-species pair`() {
        val calculator = BreedingCalculator(pals = basicPals)
        val pairs = calculator.findParentPairs(pal10)
        assertTrue(pairs.any { setOf(it.first.id, it.second.id) == setOf("pal10", "pal10") })
    }

    @Test
    fun `findParentPairs finds every formula pair that resolves to the target`() {
        val calculator = BreedingCalculator(pals = basicPals)
        val pairs = calculator.findParentPairs(pal20)
        assertTrue(pairs.any { setOf(it.first.id, it.second.id) == setOf("pal10", "pal30") })
        assertTrue(pairs.any { setOf(it.first.id, it.second.id) == setOf("pal20", "pal30") })
    }

    @Test
    fun `findParentPairs for a same-species-only pal returns only the same-species pair`() {
        val calculator = BreedingCalculator(
            pals = listOf(jetragon, pal10, pal20, pal30),
            sameSpeciesOnlyIds = setOf("jetragon")
        )
        val pairs = calculator.findParentPairs(jetragon)
        assertEquals(1, pairs.size)
        assertEquals(setOf("jetragon", "jetragon"), setOf(pairs[0].first.id, pairs[0].second.id))
    }

    @Test
    fun `findParentPairs includes special combo pairs found via reverse lookup`() {
        val calculator = BreedingCalculator(
            pals = specialComboPals,
            specialCombinations = mapOf(setOf("relaxaurus", "sparkit") to "relaxaurus_lux")
        )
        val pairs = calculator.findParentPairs(relaxaurusLux)
        assertTrue(pairs.any { setOf(it.first.id, it.second.id) == setOf("relaxaurus", "sparkit") })
    }

    // --- Integration tests against the real 1.0 dataset ---

    private fun realCalculator() = BreedingCalculator(
        pals = PalDataSource.allPals,
        specialCombinations = PalDataSource.specialCombinations,
        sameSpeciesOnlyIds = PalDataSource.sameSpeciesOnlyIds
    )

    private fun pal(id: String) = PalDataSource.allPals.first { it.id == id }

    @Test
    fun `1_0 data - Gobfin plus Suzaku produces Valentail (user-verified in-game)`() {
        // Formula check: (2550 + 1200 + 1)/2 = 1875 -> nearest is Valentail (1900).
        assertEquals(pal("valentail"), realCalculator().calculateChild(pal("gobfin"), pal("suzaku")))
    }

    @Test
    fun `1_0 data - Nitewing plus Helzephyr produces Azurobe (user-verified in-game)`() {
        assertEquals(pal("azurobe"), realCalculator().calculateChild(pal("nitewing"), pal("helzephyr")))
    }

    @Test
    fun `1_0 data - verified special combos resolve correctly`() {
        val calc = realCalculator()
        assertEquals(pal("relaxaurus_lux"), calc.calculateChild(pal("relaxaurus"), pal("sparkit")))
        assertEquals(pal("mau_cryst"), calc.calculateChild(pal("mau"), pal("pengullet")))
        assertEquals(pal("anubis"), calc.calculateChild(pal("penking"), pal("bushi")))
        assertEquals(pal("frostallion_noct"), calc.calculateChild(pal("frostallion"), pal("helzephyr")))
    }

    @Test
    fun `1_0 data - same-species-only legendaries only breed true`() {
        val calc = realCalculator()
        assertEquals(pal("jetragon"), calc.calculateChild(pal("jetragon"), pal("jetragon")))
        val pairs = calc.findParentPairs(pal("neptilius"))
        assertEquals(1, pairs.size)
        assertEquals(setOf("neptilius"), setOf(pairs[0].first.id, pairs[0].second.id))
    }

    @Test
    fun `1_0 data - every special combo child and every same-species-only id exists in the roster`() {
        val ids = PalDataSource.allPals.map { it.id }.toSet()
        PalDataSource.specialCombinations.forEach { (parents, childId) ->
            parents.forEach { assertTrue("Missing parent id $it", it in ids) }
            assertTrue("Missing child id $childId", childId in ids)
        }
        PalDataSource.sameSpeciesOnlyIds.forEach { assertTrue("Missing same-species id $it", it in ids) }
    }
}
