package com.asitkg.bmitracker.domain

import com.asitkg.bmitracker.domain.model.UnitConverter
import com.asitkg.bmitracker.domain.model.WeightUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class UnitConverterTest {

    private val tolerance = 0.001

    @Test
    fun `converts pounds to kilograms`() {
        assertEquals(45.359, UnitConverter.lbsToKg(100.0), tolerance)
    }

    @Test
    fun `converts kilograms to pounds`() {
        assertEquals(220.462, UnitConverter.kgToLbs(100.0), 0.01)
    }

    @Test
    fun `converts inches to centimetres`() {
        assertEquals(2.54, UnitConverter.inchesToCm(1.0), tolerance)
    }

    @Test
    fun `converts feet and inches to centimetres`() {
        // 5'9" is 69 inches -> 175.26 cm
        assertEquals(175.26, UnitConverter.feetAndInchesToCm(5, 9.0), tolerance)
    }

    @Test
    fun `splits centimetres into feet and inches`() {
        val result = UnitConverter.cmToFeetAndInches(175.26)
        assertEquals(5, result.feet)
        assertEquals(9.0, result.inches, tolerance)
    }

    @Test
    fun `weight conversion round trips without drift`() {
        // A unit toggle must never mutate the underlying value, so the round
        // trip has to be lossless within floating-point tolerance.
        val originalKg = 73.4
        val asLbs = UnitConverter.kgToDisplay(originalKg, WeightUnit.LBS)
        val backToKg = UnitConverter.displayToKg(asLbs, WeightUnit.LBS)
        assertEquals(originalKg, backToKg, tolerance)
    }

    @Test
    fun `height conversion round trips without drift`() {
        val originalCm = 182.9
        val split = UnitConverter.cmToFeetAndInches(originalCm)
        val backToCm = UnitConverter.feetAndInchesToCm(split.feet, split.inches)
        assertEquals(originalCm, backToCm, tolerance)
    }

    @Test
    fun `kilogram display unit is a passthrough`() {
        assertEquals(80.0, UnitConverter.kgToDisplay(80.0, WeightUnit.KG), tolerance)
        assertEquals(80.0, UnitConverter.displayToKg(80.0, WeightUnit.KG), tolerance)
    }
}
