package com.palhelper.app.data.model

/**
 * Represents a Palworld Pal and the hidden data Palworld's real breeding mechanic relies on.
 *
 * Palworld doesn't pick a breeding child randomly: every Pal has a hidden "Combi Rank"
 * (a.k.a. breeding power). When two Pals breed, the game averages their ranks and returns
 * whichever eligible Pal has the closest rank to that average (ties broken by the lower
 * Paldeck index). See [com.palhelper.app.domain.BreedingCalculator].
 *
 * @property id Stable, lowercase, snake_case identifier (e.g. "relaxaurus_lux").
 * @property displayName Human readable name shown in the UI (e.g. "Relaxaurus Lux").
 * @property breedingPower The hidden Combi Rank used by the average-rank formula. Null when
 * unknown/not documented (typically Pals only reachable via a fixed special combo).
 * @property paldeckIndex Internal game-file order, used only to break ties when two eligible
 * Pals are equidistant from a computed average rank. Null when unknown.
 * @property eligibleAsFormulaChild Whether this Pal can be the *result* of the standard
 * average-rank formula. Some variant Pals only ever come from a fixed special combo and are
 * never selected by the formula itself, even though they still have a rank value in the game data.
 * @property iconUrl Optional URL to this Pal's icon, hosted by the community wiki. Loaded live
 * over the network at display time (see [com.palhelper.app.ui.components.PalIcon]) rather than
 * bundled into the app, since Pal artwork belongs to Pocketpair. Null for Pals not covered by
 * the captured icon set; the UI falls back to a generated placeholder badge in that case.
 */
data class Pal(
    val id: String,
    val displayName: String,
    val breedingPower: Int?,
    val paldeckIndex: Int?,
    val eligibleAsFormulaChild: Boolean = true,
    val iconUrl: String? = null
)
