package com.palhelper.app.data.model.structure

import com.palhelper.app.data.translations.PortugueseTranslations

/**
 * Extension functions to get Portuguese names for structures and materials
 */
fun Structure.getPortugueseName(): String {
    return PortugueseTranslations.getStructureName(this.id)
}

fun MaterialCost.getPortugueseName(): String {
    return PortugueseTranslations.getMaterialName(this.materialId)
}
