package com.asitkg.bmitracker.domain

import com.asitkg.bmitracker.domain.model.BmiCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class BmiCategoryTest {

    @Test
    fun `classifies each band`() {
        assertEquals(BmiCategory.UNDERWEIGHT, BmiCategory.forBmi(17.0))
        assertEquals(BmiCategory.NORMAL, BmiCategory.forBmi(22.0))
        assertEquals(BmiCategory.OVERWEIGHT, BmiCategory.forBmi(27.5))
        assertEquals(BmiCategory.OBESE, BmiCategory.forBmi(35.0))
    }

    @Test
    fun `boundaries belong to the upper band`() {
        // Half-open ranges: 18.5 is normal, not underweight.
        assertEquals(BmiCategory.NORMAL, BmiCategory.forBmi(18.5))
        assertEquals(BmiCategory.OVERWEIGHT, BmiCategory.forBmi(25.0))
        assertEquals(BmiCategory.OBESE, BmiCategory.forBmi(30.0))
    }

    @Test
    fun `values just below a boundary stay in the lower band`() {
        assertEquals(BmiCategory.UNDERWEIGHT, BmiCategory.forBmi(18.49))
        assertEquals(BmiCategory.NORMAL, BmiCategory.forBmi(24.99))
        assertEquals(BmiCategory.OVERWEIGHT, BmiCategory.forBmi(29.99))
    }

    @Test
    fun `every band is contiguous with the next`() {
        // No gaps: each band's exclusive max is the next band's inclusive min,
        // which is what makes forBmi total over all non-negative values.
        val ordered = BmiCategory.entries.sortedBy { it.minInclusive }
        ordered.zipWithNext { lower, upper ->
            assertEquals(lower.maxExclusive, upper.minInclusive, 0.0)
        }
    }

    @Test
    fun `extreme values still classify`() {
        assertEquals(BmiCategory.UNDERWEIGHT, BmiCategory.forBmi(0.0))
        assertEquals(BmiCategory.OBESE, BmiCategory.forBmi(200.0))
    }
}
