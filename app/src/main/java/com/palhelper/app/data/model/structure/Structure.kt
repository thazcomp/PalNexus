package com.palhelper.app.data.model.structure

/**
 * A single crafting material and how much of it something costs (e.g. 50 "Wood").
 *
 * @property materialId Stable lowercase id (e.g. "wood", "paldium_fragment").
 * @property displayName Human-readable name shown in the UI (e.g. "Wood").
 * @property amount How many units are required.
 * @property iconUrl Optional URL to the material's icon, loaded live at display time; null falls
 * back to a generated placeholder badge (same approach as [com.palhelper.app.data.model.Pal]).
 */
data class MaterialCost(
    val materialId: String,
    val displayName: String,
    val amount: Int,
    val iconUrl: String? = null
)

/** Broad category used to group structures in the grid. */
enum class StructureCategory(val displayName: String) {
    FOUNDATION("Estrutura"),
    PRODUCT("Produção"),
    STORAGE("Armazenamento"),
    PALS("Pals"),
    INFRASTRUCTURE("Infraestrutura"),
    FOOD("Comida"),
    DEFENSE("Defesa"),
    LIGHT("Iluminação"),
    OTHER("Outros")
}

/**
 * A buildable base structure and its full material cost.
 *
 * @property id Stable lowercase id (e.g. "wooden_chest").
 * @property displayName Name shown in the UI.
 * @property category Grouping for the grid.
 * @property techLevel Tech/unlock level, when known (null for always-available pieces).
 * @property costs The materials (and amounts) required to build one of these.
 * @property iconUrl Optional icon URL, loaded live; null falls back to a placeholder badge.
 */
data class Structure(
    val id: String,
    val displayName: String,
    val category: StructureCategory,
    val techLevel: Int?,
    val costs: List<MaterialCost>,
    val iconUrl: String? = null
)
