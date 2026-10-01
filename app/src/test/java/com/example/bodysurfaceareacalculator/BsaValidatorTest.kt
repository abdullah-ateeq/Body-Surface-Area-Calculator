package com.example.bodysurfaceareacalculator

import com.example.bodysurfaceareacalculator.domain.BsaValidator
import com.example.bodysurfaceareacalculator.domain.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BsaValidatorTest {

    // 9. Zero height validation
    @Test
    fun testZeroHeightValidation() {
        val resultCm = BsaValidator.validateHeightCm("0")
        assertTrue(resultCm is ValidationResult.Error)
        assertEquals("Enter a valid height.", (resultCm as ValidationResult.Error).message)

        val resultFtIn = BsaValidator.validateHeightFeetInches("0", "0")
        assertTrue(resultFtIn is ValidationResult.Error)
    }

    // 10. Negative height validation
    @Test
    fun testNegativeHeightValidation() {
        val resultCm = BsaValidator.validateHeightCm("-170")
        assertTrue(resultCm is ValidationResult.Error)
        assertEquals("Enter a valid height.", (resultCm as ValidationResult.Error).message)

        val resultFt = BsaValidator.validateHeightFeetInches("-5", "7")
        assertTrue(resultFt is ValidationResult.Error)

        val resultIn = BsaValidator.validateHeightFeetInches("5", "-2")
        assertTrue(resultIn is ValidationResult.Error)
    }

    // 11. Zero weight validation
    @Test
    fun testZeroWeightValidation() {
        val resultKg = BsaValidator.validateWeightKg("0")
        assertTrue(resultKg is ValidationResult.Error)
        assertEquals("Enter a valid weight.", (resultKg as ValidationResult.Error).message)

        val resultLb = BsaValidator.validateWeightLb("0")
        assertTrue(resultLb is ValidationResult.Error)
        assertEquals("Enter a valid weight.", (resultLb as ValidationResult.Error).message)
    }

    // 12. Negative weight validation
    @Test
    fun testNegativeWeightValidation() {
        val resultKg = BsaValidator.validateWeightKg("-70")
        assertTrue(resultKg is ValidationResult.Error)
        assertEquals("Enter a valid weight.", (resultKg as ValidationResult.Error).message)

        val resultLb = BsaValidator.validateWeightLb("-150")
        assertTrue(resultLb is ValidationResult.Error)
        assertEquals("Enter a valid weight.", (resultLb as ValidationResult.Error).message)
    }

    // 13. Height range validation (30–300 cm)
    @Test
    fun testHeightRangeValidation() {
        // Below lower bound
        val tooShort = BsaValidator.validateHeightCm("29.9")
        assertTrue(tooShort is ValidationResult.Error)
        assertEquals("Enter a height between 30 and 300 cm.", (tooShort as ValidationResult.Error).message)

        // At lower bound
        val lowerBound = BsaValidator.validateHeightCm("30.0")
        assertTrue(lowerBound is ValidationResult.Success)
        assertEquals(30.0, (lowerBound as ValidationResult.Success).normalizedValue, 0.001)

        // At upper bound
        val upperBound = BsaValidator.validateHeightCm("300.0")
        assertTrue(upperBound is ValidationResult.Success)
        assertEquals(300.0, (upperBound as ValidationResult.Success).normalizedValue, 0.001)

        // Above upper bound
        val tooTall = BsaValidator.validateHeightCm("300.1")
        assertTrue(tooTall is ValidationResult.Error)
        assertEquals("Enter a height between 30 and 300 cm.", (tooTall as ValidationResult.Error).message)
    }

    // 14. Weight range validation (1–500 kg, 2.2–1102 lb)
    @Test
    fun testWeightRangeValidation() {
        // Below lower bound kg
        val tooLightKg = BsaValidator.validateWeightKg("0.9")
        assertTrue(tooLightKg is ValidationResult.Error)
        assertEquals("Enter a weight between 1 and 500 kg.", (tooLightKg as ValidationResult.Error).message)

        // At lower bound kg
        val lowerBoundKg = BsaValidator.validateWeightKg("1.0")
        assertTrue(lowerBoundKg is ValidationResult.Success)
        assertEquals(1.0, (lowerBoundKg as ValidationResult.Success).normalizedValue, 0.001)

        // At upper bound kg
        val upperBoundKg = BsaValidator.validateWeightKg("500.0")
        assertTrue(upperBoundKg is ValidationResult.Success)
        assertEquals(500.0, (upperBoundKg as ValidationResult.Success).normalizedValue, 0.001)

        // Above upper bound kg
        val tooHeavyKg = BsaValidator.validateWeightKg("500.1")
        assertTrue(tooHeavyKg is ValidationResult.Error)
        assertEquals("Enter a weight between 1 and 500 kg.", (tooHeavyKg as ValidationResult.Error).message)

        // Pounds range validation
        val validLb = BsaValidator.validateWeightLb("154.3")
        assertTrue(validLb is ValidationResult.Success)

        val tooLightLb = BsaValidator.validateWeightLb("1.5")
        assertTrue(tooLightLb is ValidationResult.Error)

        val tooHeavyLb = BsaValidator.validateWeightLb("1200")
        assertTrue(tooHeavyLb is ValidationResult.Error)
    }

    // Additional edge cases: empty strings, decimal points, malformed numbers
    @Test
    fun testEmptyAndMalformedInputs() {
        val emptyHeight = BsaValidator.validateHeightCm("")
        assertTrue(emptyHeight is ValidationResult.Error)
        assertEquals("Height is required.", (emptyHeight as ValidationResult.Error).message)

        val emptyWeight = BsaValidator.validateWeightKg("  ")
        assertTrue(emptyWeight is ValidationResult.Error)
        assertEquals("Weight is required.", (emptyWeight as ValidationResult.Error).message)

        val malformedHeight = BsaValidator.validateHeightCm("abc")
        assertTrue(malformedHeight is ValidationResult.Error)
        assertEquals("Enter a valid height.", (malformedHeight as ValidationResult.Error).message)

        val malformedWeight = BsaValidator.validateWeightKg("12.34.56")
        assertTrue(malformedWeight is ValidationResult.Error)
        assertEquals("Enter a valid weight.", (malformedWeight as ValidationResult.Error).message)
    }

    // Feet and inches range validation
    @Test
    fun testFeetAndInchesValidation() {
        // Valid 5 ft 10 in
        val valid = BsaValidator.validateHeightFeetInches("5", "10")
        assertTrue(valid is ValidationResult.Success)

        // Decimal inches allowed: 5 ft 7.5 in
        val decimalInches = BsaValidator.validateHeightFeetInches("5", "7.5")
        assertTrue(decimalInches is ValidationResult.Success)

        // Feet > 9 rejected
        val feetTooHigh = BsaValidator.validateHeightFeetInches("10", "2")
        assertTrue(feetTooHigh is ValidationResult.Error)

        // Inches >= 12 rejected
        val inchesTooHigh = BsaValidator.validateHeightFeetInches("5", "12")
        assertTrue(inchesTooHigh is ValidationResult.Error)
    }
}
