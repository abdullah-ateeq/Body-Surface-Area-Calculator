package com.example.bodysurfaceareacalculator.domain

object BsaConstants {
    // Conversions
    const val INCH_TO_CM = 2.54
    const val FEET_TO_INCHES = 12.0
    const val KG_TO_LB = 2.2046226218
    const val LB_TO_KG = 0.45359237

    // Practical Validation Bounds
    const val MIN_HEIGHT_CM = 30.0
    const val MAX_HEIGHT_CM = 300.0

    const val MIN_FEET = 0.0
    const val MAX_FEET = 9.0
    const val MIN_INCHES = 0.0
    const val MAX_INCHES = 11.99

    const val MIN_WEIGHT_KG = 1.0
    const val MAX_WEIGHT_KG = 500.0

    const val MIN_WEIGHT_LB = 2.2
    const val MAX_WEIGHT_LB = 1102.0

    // History
    const val MAX_HISTORY_ITEMS = 50

    // Medical Disclaimer
    const val MEDICAL_DISCLAIMER =
        "Calculation only. This tool does not provide medical advice or treatment recommendations."
}
