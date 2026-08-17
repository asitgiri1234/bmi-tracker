package com.asitkg.bmitracker.domain

import com.asitkg.bmitracker.domain.model.BmiCategory
import kotlin.math.roundToInt

/**
 * BMI arithmetic, deliberately free of Android and storage concerns so it can
 * be unit-tested directly.
 *
 * Inputs are canonical units (kg, cm); callers convert from display units via
 * [com.asitkg.bmitracker.domain.model.UnitConverter] before calling in.
 */
object BmiCalculator {

    /** Guards against division by zero and obviously bogus input. */
    private val VALID_HEIGHT_CM = 50.0..300.0
    private val VALID_WEIGHT_KG = 2.0..650.0

    /**
     * BMI = weight(kg) / height(m)².
     *
     * @return the BMI, or null when inputs fall outside a physically plausible
     * range — returning null rather than NaN forces callers to handle it.
     */
    fun calculate(weightKg: Double, heightCm: Double): Double? {
        if (heightCm !in VALID_HEIGHT_CM || weightKg !in VALID_WEIGHT_KG) return null
        val heightM = heightCm / 100.0
        return weightKg / (heightM * heightM)
    }

    fun categoryFor(weightKg: Double, heightCm: Double): BmiCategory? =
        calculate(weightKg, heightCm)?.let(BmiCategory::forBmi)

    /** Rounds to one decimal place, the conventional precision for BMI. */
    fun round(bmi: Double): Double = (bmi * 10).roundToInt() / 10.0

    /**
     * The weight range that would place this height in [BmiCategory.NORMAL],
     * used by the dashboard to show a target rather than only a verdict.
     */
    fun healthyWeightRangeKg(heightCm: Double): ClosedFloatingPointRange<Double>? {
        if (heightCm !in VALID_HEIGHT_CM) return null
        val heightM = heightCm / 100.0
        val area = heightM * heightM
        return (BmiCategory.NORMAL.minInclusive * area)..(BmiCategory.NORMAL.maxExclusive * area)
    }
}
