package com.example.bodysurfaceareacalculator.data.model

data class BsaInput(
    val heightValue: Double,
    val heightUnit: HeightUnit,
    val feet: Double? = null,
    val inches: Double? = null,
    val weightValue: Double,
    val weightUnit: WeightUnit,
    val formula: BsaFormula
)
