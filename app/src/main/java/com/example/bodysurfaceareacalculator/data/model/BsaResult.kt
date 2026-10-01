package com.example.bodysurfaceareacalculator.data.model

import java.util.Locale

data class BsaResult(
    val heightCm: Double,
    val weightKg: Double,
    val bsaM2: Double,
    val formula: BsaFormula,
    val comparisonBsaM2: Double? = null
) {
    val formattedBsa: String
        get() = String.format(Locale.US, "%.2f m²", bsaM2)

    val formattedComparisonBsa: String?
        get() = comparisonBsaM2?.let { String.format(Locale.US, "%.2f m²", it) }

    val formattedHeightCm: String
        get() = String.format(Locale.US, "%.1f cm", heightCm)

    val formattedWeightKg: String
        get() = String.format(Locale.US, "%.1f kg", weightKg)
}
