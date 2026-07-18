package com.palhelper.app.ui.structure

import androidx.annotation.StringRes
import com.palhelper.app.R
import com.palhelper.app.data.model.structure.StructureCategory

@StringRes
fun StructureCategory.getDisplayNameRes(): Int = when (this) {
    StructureCategory.FOUNDATION -> R.string.category_foundation
    StructureCategory.PRODUCT -> R.string.category_product
    StructureCategory.STORAGE -> R.string.category_storage
    StructureCategory.PALS -> R.string.category_pals
    StructureCategory.INFRASTRUCTURE -> R.string.category_infrastructure
    StructureCategory.FOOD -> R.string.category_food
    StructureCategory.DEFENSE -> R.string.category_defense
    StructureCategory.LIGHT -> R.string.category_light
    StructureCategory.OTHER -> R.string.category_other
}
