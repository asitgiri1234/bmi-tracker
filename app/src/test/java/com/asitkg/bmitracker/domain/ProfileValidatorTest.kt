package com.asitkg.bmitracker.domain

import com.asitkg.bmitracker.domain.model.HeightUnit
import com.asitkg.bmitracker.domain.model.WeightUnit
import com.asitkg.bmitracker.domain.validation.ProfileValidator
import com.asitkg.bmitracker.domain.validation.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ProfileValidatorTest {

    private val tolerance = 0.01
    private val today = LocalDate.of(2026, 8, 17)

    // --- name ---

    @Test
    fun `rejects a blank name`() {
        assertTrue(ProfileValidator.validateName("   ") is ValidationResult.Invalid)
    }

    @Test
    fun `trims a valid name`() {
        val result = ProfileValidator.validateName("  Asit  ")
        assertEquals("Asit", result.valueOrNull)
    }

    @Test
    fun `rejects an over-long name`() {
        val tooLong = "a".repeat(ProfileValidator.MAX_NAME_LENGTH + 1)
        assertTrue(ProfileValidator.validateName(tooLong) is ValidationResult.Invalid)
    }

    // --- weight ---

    @Test
    fun `rejects non-numeric weight`() {
        assertNotNull(ProfileValidator.validateWeight("abc", WeightUnit.KG).errorMessage)
    }

    @Test
    fun `rejects empty weight`() {
        assertNotNull(ProfileValidator.validateWeight("", WeightUnit.KG).errorMessage)
    }

    @Test
    fun `rejects zero and negative weight`() {
        assertNotNull(ProfileValidator.validateWeight("0", WeightUnit.KG).errorMessage)
        assertNotNull(ProfileValidator.validateWeight("-5", WeightUnit.KG).errorMessage)
    }

    @Test
    fun `accepts a valid weight in kilograms`() {
        val result = ProfileValidator.validateWeight("70.5", WeightUnit.KG)
        assertEquals(70.5, result.valueOrNull!!, tolerance)
    }

    @Test
    fun `converts pounds to kilograms on validation`() {
        val result = ProfileValidator.validateWeight("154", WeightUnit.LBS)
        assertEquals(69.85, result.valueOrNull!!, 0.05)
    }

    @Test
    fun `accepts a comma decimal separator`() {
        // Some keyboard locales emit a comma and give the user no period key.
        val result = ProfileValidator.validateWeight("70,5", WeightUnit.KG)
        assertEquals(70.5, result.valueOrNull!!, tolerance)
    }

    @Test
    fun `applies the same limits regardless of unit`() {
        // 2000 lbs is ~907 kg, over the limit — it must not pass simply
        // because the number looks smaller in kilograms.
        assertNotNull(ProfileValidator.validateWeight("2000", WeightUnit.LBS).errorMessage)
    }

    // --- height ---

    @Test
    fun `rejects out-of-range height in centimetres`() {
        assertNotNull(ProfileValidator.validateHeightCm("20").errorMessage)
        assertNotNull(ProfileValidator.validateHeightCm("400").errorMessage)
    }

    @Test
    fun `accepts a valid height in centimetres`() {
        assertEquals(175.0, ProfileValidator.validateHeightCm("175").valueOrNull!!, tolerance)
    }

    @Test
    fun `converts feet and inches to centimetres`() {
        val result = ProfileValidator.validateHeightFeetInches("5", "9")
        assertEquals(175.26, result.valueOrNull!!, tolerance)
    }

    @Test
    fun `treats blank inches as zero`() {
        val result = ProfileValidator.validateHeightFeetInches("6", "")
        assertEquals(182.88, result.valueOrNull!!, tolerance)
    }

    @Test
    fun `rejects twelve or more inches`() {
        assertNotNull(ProfileValidator.validateHeightFeetInches("5", "12").errorMessage)
    }

    @Test
    fun `rejects negative feet`() {
        assertNotNull(ProfileValidator.validateHeightFeetInches("-1", "0").errorMessage)
    }

    @Test
    fun `dispatches height validation on the selected unit`() {
        val cm = ProfileValidator.validateHeight(HeightUnit.CM, "175", "", "")
        assertEquals(175.0, cm.valueOrNull!!, tolerance)

        val ft = ProfileValidator.validateHeight(HeightUnit.FEET_INCHES, "", "5", "9")
        assertEquals(175.26, ft.valueOrNull!!, tolerance)
    }

    // --- date of birth ---

    @Test
    fun `date of birth is optional`() {
        val result = ProfileValidator.validateDateOfBirth(null, today)
        assertTrue(result is ValidationResult.Valid)
        assertNull(result.valueOrNull)
    }

    @Test
    fun `rejects a future date of birth`() {
        val tomorrow = today.plusDays(1)
        assertNotNull(ProfileValidator.validateDateOfBirth(tomorrow, today).errorMessage)
    }

    @Test
    fun `accepts today as a date of birth`() {
        assertNull(ProfileValidator.validateDateOfBirth(today, today).errorMessage)
    }

    @Test
    fun `rejects an implausibly old date of birth`() {
        val ancient = today.minusYears(ProfileValidator.MAX_AGE_YEARS + 1L)
        assertNotNull(ProfileValidator.validateDateOfBirth(ancient, today).errorMessage)
    }

    @Test
    fun `accepts a plausible date of birth`() {
        val born = LocalDate.of(2000, 5, 20)
        assertEquals(born, ProfileValidator.validateDateOfBirth(born, today).valueOrNull)
    }
}
