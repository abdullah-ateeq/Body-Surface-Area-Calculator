package com.example.bodysurfaceareacalculator.domain

sealed class ValidationResult {
    data class Success(val normalizedValue: Double) : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

object BsaValidator {

    fun validateHeightCm(input: String): ValidationResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult.Error("Height is required.")
        }
        val value = trimmed.toDoubleOrNull()
            ?: return ValidationResult.Error("Enter a valid height.")

        if (value.isNaN() || value.isInfinite() || value <= 0.0) {
            return ValidationResult.Error("Enter a valid height.")
        }
        if (value < BsaConstants.MIN_HEIGHT_CM || value > BsaConstants.MAX_HEIGHT_CM) {
            return ValidationResult.Error("Enter a height between 30 and 300 cm.")
        }
        return ValidationResult.Success(value)
    }

    fun validateHeightFeetInches(feetInput: String, inchesInput: String): ValidationResult {
        val trimmedFeet = feetInput.trim()
        val trimmedInches = inchesInput.trim()

        if (trimmedFeet.isEmpty() && trimmedInches.isEmpty()) {
            return ValidationResult.Error("Height is required.")
        }

        val feet = trimmedFeet.toDoubleOrNull()
        val inches = if (trimmedInches.isEmpty()) 0.0 else trimmedInches.toDoubleOrNull()

        if (feet == null || inches == null) {
            return ValidationResult.Error("Enter valid feet and inches.")
        }

        if (feet.isNaN() || inches.isNaN() || feet.isInfinite() || inches.isInfinite()) {
            return ValidationResult.Error("Enter valid feet and inches.")
        }

        if (feet < BsaConstants.MIN_FEET || feet > BsaConstants.MAX_FEET) {
            return ValidationResult.Error("Enter valid feet and inches.")
        }

        if (inches < BsaConstants.MIN_INCHES || inches > BsaConstants.MAX_INCHES) {
            return ValidationResult.Error("Enter valid feet and inches.")
        }

        val totalInches = (feet * BsaConstants.FEET_TO_INCHES) + inches
        val heightCm = totalInches * BsaConstants.INCH_TO_CM

        if (heightCm <= 0.0) {
            return ValidationResult.Error("Enter a valid height.")
        }

        if (heightCm < BsaConstants.MIN_HEIGHT_CM || heightCm > BsaConstants.MAX_HEIGHT_CM) {
            return ValidationResult.Error("Enter a height between 30 and 300 cm.")
        }

        return ValidationResult.Success(heightCm)
    }

    fun validateWeightKg(input: String): ValidationResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult.Error("Weight is required.")
        }
        val value = trimmed.toDoubleOrNull()
            ?: return ValidationResult.Error("Enter a valid weight.")

        if (value.isNaN() || value.isInfinite() || value <= 0.0) {
            return ValidationResult.Error("Enter a valid weight.")
        }
        if (value < BsaConstants.MIN_WEIGHT_KG || value > BsaConstants.MAX_WEIGHT_KG) {
            return ValidationResult.Error("Enter a weight between 1 and 500 kg.")
        }
        return ValidationResult.Success(value)
    }

    fun validateWeightLb(input: String): ValidationResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult.Error("Weight is required.")
        }
        val value = trimmed.toDoubleOrNull()
            ?: return ValidationResult.Error("Enter a valid weight.")

        if (value.isNaN() || value.isInfinite() || value <= 0.0) {
            return ValidationResult.Error("Enter a valid weight.")
        }
        if (value < BsaConstants.MIN_WEIGHT_LB || value > BsaConstants.MAX_WEIGHT_LB) {
            return ValidationResult.Error("Enter a weight between 1 and 500 kg.")
        }

        val normalizedKg = BsaCalculator.weightToKilogramsFromLb(value)
        if (normalizedKg < BsaConstants.MIN_WEIGHT_KG || normalizedKg > BsaConstants.MAX_WEIGHT_KG) {
            return ValidationResult.Error("Enter a weight between 1 and 500 kg.")
        }

        return ValidationResult.Success(normalizedKg)
    }
}
