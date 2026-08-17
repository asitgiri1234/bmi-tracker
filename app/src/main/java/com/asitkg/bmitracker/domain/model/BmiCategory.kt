package com.asitkg.bmitracker.domain.model

/**
 * WHO BMI classification.
 *
 * Boundaries are half-open (`min <= bmi < max`) so no value can fall between
 * two categories — the common bug when thresholds are written as 18.5–24.9.
 */
enum class BmiCategory(
    val label: String,
    val minInclusive: Double,
    val maxExclusive: Double,
) {
    UNDERWEIGHT("Underweight", 0.0, 18.5),
    NORMAL("Normal weight", 18.5, 25.0),
    OVERWEIGHT("Overweight", 25.0, 30.0),
    OBESE("Obese", 30.0, Double.POSITIVE_INFINITY),
    ;

    companion object {
        fun forBmi(bmi: Double): BmiCategory =
            entries.first { bmi >= it.minInclusive && bmi < it.maxExclusive }
    }
}
