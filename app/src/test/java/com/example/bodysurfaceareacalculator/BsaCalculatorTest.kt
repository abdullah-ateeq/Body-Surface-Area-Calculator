package com.example.bodysurfaceareacalculator

import com.example.bodysurfaceareacalculator.data.model.BsaFormula
import com.example.bodysurfaceareacalculator.domain.BsaCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class BsaCalculatorTest {

    private val tolerance = 0.01

    // 1. Centimeter input
    @Test
    fun testCentimeterInput() {
        val cm = 175.5
        val normalized = BsaCalculator.heightToCentimeters(cm)
        assertEquals(175.5, normalized, 0.0001)
    }

    // 2. Feet/inches conversion
    @Test
    fun testFeetInchesConversion() {
        // 5 feet 7 inches = (5 * 12 + 7) = 67 inches
        // 67 * 2.54 = 170.18 cm
        val converted = BsaCalculator.heightToCentimeters(5.0, 7.0)
        assertEquals(170.18, converted, 0.001)

        // 6 feet 0 inches = 72 inches * 2.54 = 182.88 cm
        val sixFeet = BsaCalculator.heightToCentimeters(6.0, 0.0)
        assertEquals(182.88, sixFeet, 0.001)
    }

    // 3. Kilogram input
    @Test
    fun testKilogramInput() {
        val kg = 68.4
        val normalized = BsaCalculator.weightToKilograms(kg)
        assertEquals(68.4, normalized, 0.0001)
    }

    // 4. Pound conversion
    @Test
    fun testPoundConversion() {
        // 154.3236 lb -> 70.0 kg (154.3236 * 0.45359237 = 70.0000...)
        val pounds = 154.3236
        val kg = BsaCalculator.weightToKilogramsFromLb(pounds)
        assertEquals(70.0, kg, 0.01)

        // 220.462 lb -> 100 kg
        val hundredKg = BsaCalculator.weightToKilogramsFromLb(220.462)
        assertEquals(100.0, hundredKg, 0.05)
    }

    // 5. Mosteller calculation (Known example: Height 170 cm, Weight 70 kg => ~1.82 m²)
    @Test
    fun testMostellerCalculationKnownExample() {
        val heightCm = 170.0
        val weightKg = 70.0
        val bsa = BsaCalculator.calculateMosteller(heightCm, weightKg)

        // Exact formula: sqrt((170 * 70) / 3600) = sqrt(11900 / 3600) = 1.8181186857...
        // Rounds to 1.82 m²
        assertTrue(
            "Expected BSA around 1.82 m² but got $bsa",
            abs(bsa - 1.818) < tolerance
        )
        val rounded = BsaCalculator.roundTo2Decimals(bsa)
        assertEquals(1.82, rounded, 0.001)
        assertEquals("1.82 m²", BsaCalculator.formatBsa(bsa))
    }

    // 6. Du Bois calculation
    @Test
    fun testDuBoisCalculation() {
        val heightCm = 170.0
        val weightKg = 70.0
        val bsa = BsaCalculator.calculateDuBois(heightCm, weightKg)

        // Formula: 0.007184 * 170^0.725 * 70^0.425 = 1.8096...
        // Rounds to 1.81 m²
        assertTrue(
            "Expected Du Bois BSA around 1.81 m² but got $bsa",
            abs(bsa - 1.81) < tolerance
        )
        val rounded = BsaCalculator.roundTo2Decimals(bsa)
        assertEquals(1.81, rounded, 0.001)
    }

    // 7. Formula selection
    @Test
    fun testFormulaSelection() {
        val heightCm = 180.0
        val weightKg = 80.0

        val mostellerResult = BsaCalculator.calculateBsa(heightCm, weightKg, BsaFormula.MOSTELLER)
        val duBoisResult = BsaCalculator.calculateBsa(heightCm, weightKg, BsaFormula.DU_BOIS)

        assertEquals(BsaCalculator.calculateMosteller(heightCm, weightKg), mostellerResult, 0.0001)
        assertEquals(BsaCalculator.calculateDuBois(heightCm, weightKg), duBoisResult, 0.0001)
    }

    // 8. Formula comparison
    @Test
    fun testFormulaComparison() {
        val heightCm = 170.0
        val weightKg = 70.0

        // When Mosteller is selected, comparison should yield Du Bois
        val compForMosteller = BsaCalculator.calculateComparisonBsa(heightCm, weightKg, BsaFormula.MOSTELLER)
        val expectedDuBois = BsaCalculator.calculateDuBois(heightCm, weightKg)
        assertEquals(expectedDuBois, compForMosteller, 0.0001)

        // When Du Bois is selected, comparison should yield Mosteller
        val compForDuBois = BsaCalculator.calculateComparisonBsa(heightCm, weightKg, BsaFormula.DU_BOIS)
        val expectedMosteller = BsaCalculator.calculateMosteller(heightCm, weightKg)
        assertEquals(expectedMosteller, compForDuBois, 0.0001)
    }

    // 15. Decimal values handling
    @Test
    fun testDecimalValues() {
        val heightCm = 165.75
        val weightKg = 62.35
        val bsa = BsaCalculator.calculateMosteller(heightCm, weightKg)

        assertTrue(bsa > 1.0 && bsa < 3.0)
        val formatted = BsaCalculator.formatBsa(bsa)
        assertTrue(formatted.endsWith(" m²"))
    }
}
