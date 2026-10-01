package com.example.bodysurfaceareacalculator.data.model

import java.util.Locale
import java.util.UUID

data class CalculationHistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val formattedDateTime: String,
    val heightDisplay: String,
    val heightUnit: HeightUnit,
    val weightDisplay: String,
    val weightUnit: WeightUnit,
    val heightCm: Double,
    val weightKg: Double,
    val formula: BsaFormula,
    val bsaM2: Double,
    val comparisonBsaM2: Double? = null
) {
    val formattedBsa: String
        get() = String.format(Locale.US, "%.2f m²", bsaM2)

    val formattedComparisonBsa: String?
        get() = comparisonBsaM2?.let { String.format(Locale.US, "%.2f m²", it) }
}
