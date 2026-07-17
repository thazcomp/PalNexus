package com.palhelper.app.domain

import com.palhelper.app.data.model.Pal
import kotlin.math.abs

/**
 * Implements Palworld's real (1.0) breeding mechanic:
 *
 *  1. Two Pals of the same species always produce that same species.
 *  2. Certain parent pairs are "special combos" that always override the formula below
 *     (e.g. Relaxaurus + Sparkit -> Relaxaurus Lux).
 *  3. A handful of Pals can only ever be bred from two parents of their own species
 *     (see [sameSpeciesOnlyIds]).
 *  4. Otherwise: `childRank = floor((rankA + rankB + 1) / 2)`, and the resulting species is
 *     whichever *formula-eligible* Pal has a Combi Rank closest to that value. Ties are broken
 *     by the lowest Paldeck index (i.e. whichever Pal comes first in the game's internal order).
 *
 * Source: Palworld Wiki, "Breeding" article (community-documented, current as of the 1.0 patch).
 *
 * @param pals The full Pal roster to consider, both as possible parents and possible children.
 * @param specialCombinations Maps an unordered pair of parent ids to the id of the child they
 * always produce, overriding the standard formula.
 * @param sameSpeciesOnlyIds Ids of Pals that can only be produced by breeding two parents of
 * that same species (their formula-computed result, if any, is never used).
 */
class BreedingCalculator(
    pals: List<Pal>,
    private val specialCombinations: Map<Set<String>, String> = emptyMap(),
    private val sameSpeciesOnlyIds: Set<String> = emptySet()
) {

    private val palsById: Map<String, Pal> = pals.associateBy { it.id }

    private val formulaEligiblePals: List<Pal> = pals
        .filter { it.eligibleAsFormulaChild && it.breedingPower != null }
        .sortedBy { it.breedingPower }

    /** Computes the child species that results from breeding [parentA] with [parentB]. */
    fun calculateChild(parentA: Pal, parentB: Pal): Pal? {
        if (parentA.id == parentB.id) return parentA

        specialCombinations[setOf(parentA.id, parentB.id)]?.let { childId ->
            return palsById[childId]
        }

        val powerA = parentA.breedingPower ?: return null
        val powerB = parentB.breedingPower ?: return null
        val childRank = (powerA + powerB + 1) / 2

        return nearestEligiblePal(childRank)
    }

    /** Finds every parent pair (special combos, same-species, and formula matches) that produces [child]. */
    fun findParentPairs(child: Pal): List<Pair<Pal, Pal>> {
        val results = linkedSetOf<Pair<Pal, Pal>>()

        specialCombinations.forEach { (parentIds, childId) ->
            if (childId == child.id) {
                val ids = parentIds.toList()
                val a = palsById[ids[0]]
                val b = palsById[ids.getOrElse(1) { ids[0] }]
                if (a != null && b != null) results.add(orderedPair(a, b))
            }
        }

        results.add(orderedPair(child, child))

        if (child.id in sameSpeciesOnlyIds) {
            return results.toList()
        }

        if (child.breedingPower != null && child.eligibleAsFormulaChild) {
            for (i in formulaEligiblePals.indices) {
                for (j in i until formulaEligiblePals.size) {
                    val a = formulaEligiblePals[i]
                    val b = formulaEligiblePals[j]
                    if (a.id == b.id) continue
                    if (setOf(a.id, b.id) in specialCombinations.keys) continue

                    val rank = (a.breedingPower!! + b.breedingPower!! + 1) / 2
                    if (nearestEligiblePal(rank)?.id == child.id) {
                        results.add(orderedPair(a, b))
                    }
                }
            }
        }

        return results.toList()
    }

    private fun nearestEligiblePal(targetRank: Int): Pal? {
        if (formulaEligiblePals.isEmpty()) return null

        var lo = 0
        var hi = formulaEligiblePals.size - 1
        while (lo < hi) {
            val mid = (lo + hi) / 2
            if (formulaEligiblePals[mid].breedingPower!! < targetRank) lo = mid + 1 else hi = mid
        }

        val candidateIndices = listOfNotNull(lo - 1, lo, lo + 1).filter { it in formulaEligiblePals.indices }
        val candidates = candidateIndices.map { formulaEligiblePals[it] }

        val bestDistance = candidates.minOf { abs(it.breedingPower!! - targetRank) }
        return candidates
            .filter { abs(it.breedingPower!! - targetRank) == bestDistance }
            .minByOrNull { it.paldeckIndex ?: Int.MAX_VALUE }
    }

    private fun orderedPair(a: Pal, b: Pal): Pair<Pal, Pal> =
        if (a.id <= b.id) a to b else b to a
}
