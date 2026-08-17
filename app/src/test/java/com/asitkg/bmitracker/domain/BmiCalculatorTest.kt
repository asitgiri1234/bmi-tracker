package com.asitkg.bmitracker.domain

import com.asitkg.bmitracker.domain.model.BmiCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BmiCalculatorTest {

    private val tolerance = 0.01

    @Test
    fun `calculates BMI from kilograms and centimetres`() {
        // 70 kg at 1.75 m -> 70 / 3.0625 = 22.857...
        val bmi = BmiCalculator.calculate(weightKg = 70.0, heightCm = 175.0)
        assertNotNull(bmi)
        assertEquals(22.86, bmi!!, tolerance)
    }

    @Test
    fun `rounds to one decimal place`() {
        assertEquals(22.9, BmiCalculator.round(22.857), tolerance)
        assertEquals(25.0, BmiCalculator.round(24.96), tolerance)
    }

    @Test
    fun `rejects implausible height`() {
        assertNull(BmiCalculator.calculate(weightKg = 70.0, heightCm = 0.0))
        assertNull(BmiCalculator.calculate(weightKg = 70.0, heightCm = 400.0))
    }

    @Test
    fun `rejects implausible weight`() {
        assertNull(BmiCalculator.calculate(weightKg = 0.0, heightCm = 175.0))
        assertNull(BmiCalculator.calculate(weightKg = 900.0, heightCm = 175.0))
    }

    @Test
    fun `zero height cannot produce infinity`() {
        // Guards the division-by-zero path explicitly rather than relying on
        // the caller to notice an infinite result.
        assertNull(BmiCalculator.calculate(weightKg = 70.0, heightCm = 0.0))
    }

    @Test
    fun `categorises a normal weight`() {
        val category = BmiCalculator.categoryFor(weightKg = 70.0, heightCm = 175.0)
        assertEquals(BmiCategory.NORMAL, category)
    }

    @Test
    fun `healthy weight range brackets the normal category`() {
        val range = BmiCalculator.healthyWeightRangeKg(heightCm = 175.0)
        assertNotNull(range)

        // Both ends must themselves classify as NORMAL (upper end is exclusive,
        // so step just inside it).
        val lower = BmiCalculator.calculate(range!!.start, 175.0)!!
        val upper = BmiCalculator.calculate(range.endInclusive - 0.01, 175.0)!!
        assertEquals(BmiCategory.NORMAL, BmiCategory.forBmi(lower))
        assertEquals(BmiCategory.NORMAL, BmiCategory.forBmi(upper))
    }

    @Test
    fun `healthy weight range is null for implausible height`() {
        assertNull(BmiCalculator.healthyWeightRangeKg(heightCm = 10.0))
    }

    @Test
    fun `taller person needs more weight for the same BMI`() {
        val shortRange = BmiCalculator.healthyWeightRangeKg(160.0)!!
        val tallRange = BmiCalculator.healthyWeightRangeKg(190.0)!!
        assertTrue(tallRange.start > shortRange.start)
    }
}
