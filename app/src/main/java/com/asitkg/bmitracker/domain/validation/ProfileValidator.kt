package com.asitkg.bmitracker.domain.validation

import com.asitkg.bmitracker.domain.model.HeightUnit
import com.asitkg.bmitracker.domain.model.UnitConverter
import com.asitkg.bmitracker.domain.model.WeightUnit
import java.time.LocalDate
import java.time.Period

/**
 * Input validation for the user details form.
 *
 * Everything is validated in canonical units after conversion, so the same
 * plausibility limits apply whether the user typed kilograms or pounds — a
 * value that is rejected in one unit cannot sneak through in the other.
 */
object ProfileValidator {

    const val MAX_NAME_LENGTH = 40
    const val MAX_AGE_YEARS = 120

    private val VALID_HEIGHT_CM = 50.0..300.0
    private val VALID_WEIGHT_KG = 2.0..650.0

    fun validateName(raw: String): ValidationResult<String> {
        val name = raw.trim()
        return when {
            name.isEmpty() -> ValidationResult.Invalid("Name is required")
            name.length > MAX_NAME_LENGTH ->
                ValidationResult.Invalid("Name must be $MAX_NAME_LENGTH characters or fewer")

            else -> ValidationResult.Valid(name)
        }
    }

    /** @return the weight in canonical kilograms. */
    fun validateWeight(raw: String, unit: WeightUnit): ValidationResult<Double> {
        val parsed = parsePositiveNumber(raw, "weight")
        if (parsed is ValidationResult.Invalid) return parsed

        val kg = UnitConverter.displayToKg(parsed.valueOrNull!!, unit)
        if (kg !in VALID_WEIGHT_KG) {
            val low = UnitConverter.kgToDisplay(VALID_WEIGHT_KG.start, unit)
            val high = UnitConverter.kgToDisplay(VALID_WEIGHT_KG.endInclusive, unit)
            return ValidationResult.Invalid(
                "Enter a weight between ${format(low)} and ${format(high)} ${unit.label}",
            )
        }
        return ValidationResult.Valid(kg)
    }

    /** @return the height in canonical centimetres. */
    fun validateHeightCm(raw: String): ValidationResult<Double> {
        val parsed = parsePositiveNumber(raw, "height")
        if (parsed is ValidationResult.Invalid) return parsed

        val cm = parsed.valueOrNull!!
        return if (cm in VALID_HEIGHT_CM) {
            ValidationResult.Valid(cm)
        } else {
            ValidationResult.Invalid("Enter a height between 50 and 300 cm")
        }
    }

    /** @return the height in canonical centimetres. */
    fun validateHeightFeetInches(rawFeet: String, rawInches: String): ValidationResult<Double> {
        val feet = rawFeet.trim().toIntOrNull()
            ?: return ValidationResult.Invalid("Enter feet as a whole number")
        if (feet < 0) return ValidationResult.Invalid("Feet cannot be negative")

        // Inches may be blank — 6 feet exactly is a normal thing to type.
        val inchesText = rawInches.trim()
        val inches = if (inchesText.isEmpty()) 0.0 else inchesText.toDoubleOrNull()
            ?: return ValidationResult.Invalid("Enter inches as a number")

        if (inches < 0) return ValidationResult.Invalid("Inches cannot be negative")
        if (inches >= UnitConverter.INCHES_PER_FOOT) {
            return ValidationResult.Invalid("Inches must be less than 12 — add to feet instead")
        }

        val cm = UnitConverter.feetAndInchesToCm(feet, inches)
        return if (cm in VALID_HEIGHT_CM) {
            ValidationResult.Valid(cm)
        } else {
            ValidationResult.Invalid("Enter a height between 1'8\" and 9'10\"")
        }
    }

    fun validateHeight(
        unit: HeightUnit,
        rawCm: String,
        rawFeet: String,
        rawInches: String,
    ): ValidationResult<Double> = when (unit) {
        HeightUnit.CM -> validateHeightCm(rawCm)
        HeightUnit.FEET_INCHES -> validateHeightFeetInches(rawFeet, rawInches)
    }

    /**
     * Date of birth is optional, so a null [date] is valid. A supplied date
     * must be in the past and within a plausible human lifespan.
     */
    fun validateDateOfBirth(
        date: LocalDate?,
        today: LocalDate = LocalDate.now(),
    ): ValidationResult<LocalDate?> = when {
        date == null -> ValidationResult.Valid(null)
        date.isAfter(today) -> ValidationResult.Invalid("Date of birth cannot be in the future")
        Period.between(date, today).years > MAX_AGE_YEARS ->
            ValidationResult.Invalid("Enter a date within the last $MAX_AGE_YEARS years")

        else -> ValidationResult.Valid(date)
    }

    /**
     * Accepts both `.` and `,` as the decimal separator, since some keyboard
     * locales emit a comma and the user has no way to type a period.
     */
    private fun parsePositiveNumber(raw: String, fieldName: String): ValidationResult<Double> {
        val text = raw.trim().replace(',', '.')
        if (text.isEmpty()) return ValidationResult.Invalid("Enter your $fieldName")

        val value = text.toDoubleOrNull()
            ?: return ValidationResult.Invalid("Enter $fieldName as a number")
        if (value.isNaN() || value.isInfinite()) {
            return ValidationResult.Invalid("Enter $fieldName as a number")
        }
        if (value <= 0.0) return ValidationResult.Invalid("Enter a $fieldName greater than zero")
        return ValidationResult.Valid(value)
    }

    private fun format(value: Double): String =
        if (value % 1.0 == 0.0) value.toInt().toString() else String.format("%.1f", value)
}
