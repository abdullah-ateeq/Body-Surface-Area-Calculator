package com.example.bodysurfaceareacalculator.domain

import com.example.bodysurfaceareacalculator.data.model.BsaFormula
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt

object BsaCalculator {

    /**
     * Normalizes height in centimeters.
     */
    fun heightToCentimeters(cm: Double): Double = cm

    /**
     * Converts feet and inches to centimeters.
     * totalInches = feet * 12 + inches
     * heightCm = totalInches * 2.54
     */
    fun heightToCentimeters(feet: Double, inches: Double): Double {
        val totalInches = (feet * BsaConstants.FEET_TO_INCHES) + inches
        return totalInches * BsaConstants.INCH_TO_CM
    }

    /**
     * Normalizes weight in kilograms.
     */
    fun weightToKilograms(kg: Double): Double = kg

    /**
     * Converts pounds to kilograms.
     * weightKg = lb * 0.45359237
     */
    fun weightToKilogramsFromLb(lb: Double): Double {
        return lb * BsaConstants.LB_TO_KG
    }

    /**
     * Converts kilograms to pounds.
     */
    fun weightToPounds(kg: Double): Double {
        return kg * BsaConstants.KG_TO_LB
    }

    /**
     * Mosteller Formula:
     * BSA = √((Height(cm) × Weight(kg)) / 3600)
     */
    fun calculateMosteller(heightCm: Double, weightKg: Double): Double {
        require(heightCm > 0.0 && weightKg > 0.0) {
            "Height and weight must be positive numeric values."
        }
        return sqrt((heightCm * weightKg) / 3600.0)
    }

    /**
     * Du Bois & Du Bois Formula:
     * BSA = 0.007184 × Height(cm)^0.725 × Weight(kg)^0.425
     */
    fun calculateDuBois(heightCm: Double, weightKg: Double): Double {
        require(heightCm > 0.0 && weightKg > 0.0) {
            "Height and weight must be positive numeric values."
        }
        return 0.007184 * heightCm.pow(0.725) * weightKg.pow(0.425)
    }

    /**
     * Calculates BSA for the selected formula using normalized metric inputs.
     */
    fun calculateBsa(
        heightCm: Double,
        weightKg: Double,
        formula: BsaFormula
    ): Double {
        return when (formula) {
            BsaFormula.MOSTELLER -> calculateMosteller(heightCm, weightKg)
            BsaFormula.DU_BOIS -> calculateDuBois(heightCm, weightKg)
        }
    }

    /**
     * Calculates the alternative comparison formula result.
     */
    fun calculateComparisonBsa(
        heightCm: Double,
        weightKg: Double,
        selectedFormula: BsaFormula
    ): Double {
        val alternativeFormula = when (selectedFormula) {
            BsaFormula.MOSTELLER -> BsaFormula.DU_BOIS
            BsaFormula.DU_BOIS -> BsaFormula.MOSTELLER
        }
        return calculateBsa(heightCm, weightKg, alternativeFormula)
    }

    /**
     * Rounds a double value to 2 decimal places using half-up rounding.
     */
    fun roundTo2Decimals(value: Double): Double {
        if (value.isNaN() || value.isInfinite()) return 0.0
        return BigDecimal(value.toString())
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }

    /**
     * Formats BSA value to 2 decimal places with "m²" suffix.
     * Example: 1.87 m²
     */
    fun formatBsa(bsaM2: Double): String {
        return String.format(Locale.US, "%.2f m²", bsaM2)
    }
}
