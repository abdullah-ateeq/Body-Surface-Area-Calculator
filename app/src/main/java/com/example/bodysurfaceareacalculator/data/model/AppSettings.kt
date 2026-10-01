package com.example.bodysurfaceareacalculator.data.model

data class AppSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val defaultHeightUnit: HeightUnit = HeightUnit.CENTIMETERS,
    val defaultWeightUnit: WeightUnit = WeightUnit.KILOGRAMS,
    val defaultFormula: BsaFormula = BsaFormula.MOSTELLER,
    val showFormulaComparison: Boolean = false,
    val hapticFeedback: Boolean = true,
    val soundEffects: Boolean = false
)
