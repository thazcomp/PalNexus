package com.palhelper.app.domain.structure

import com.palhelper.app.data.model.structure.MaterialCost
import com.palhelper.app.data.model.structure.Structure

/**
 * Sums the materials needed to build a selection of structures, each with a chosen quantity.
 * This is the core of the resource calculator: pick a set of buildings, get one combined
 * shopping list of everything you need.
 */
class ResourceCalculator {

    /**
     * @param selection Maps each chosen [Structure] to how many of it to build.
     * @return One [MaterialCost] per distinct material, with amounts summed across the whole
     * selection, sorted by descending amount. Structures with quantity <= 0 are ignored, and
     * each material keeps the display name / icon from the first structure that referenced it.
     */
    fun totalMaterials(selection: Map<Structure, Int>): List<MaterialCost> {
        val aggregated = LinkedHashMap<String, MaterialCost>()

        selection.forEach { (structure, quantity) ->
            if (quantity <= 0) return@forEach
            structure.costs.forEach { cost ->
                val existing = aggregated[cost.materialId]
                aggregated[cost.materialId] = if (existing == null) {
                    cost.copy(amount = cost.amount * quantity)
                } else {
                    existing.copy(amount = existing.amount + cost.amount * quantity)
                }
            }
        }

        return aggregated.values.sortedByDescending { it.amount }
    }
}
