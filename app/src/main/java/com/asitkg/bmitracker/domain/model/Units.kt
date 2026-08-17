package com.asitkg.bmitracker.domain.model

/**
 * Display units.
 *
 * Weight is always persisted in kilograms and height in centimetres; these
 * enums only describe how a value is shown to and entered by the user. Keeping
 * storage canonical means toggling a unit can never alter stored data.
 */
enum class WeightUnit(val label: String) {
    KG("kg"),
    LBS("lbs"),
}

enum class HeightUnit(val label: String) {
    CM("cm"),
    FEET_INCHES("ft/in"),
}

/** Exact conversion factors, as defined by the international pound and inch. */
object UnitConverter {

    const val KG_PER_LB = 0.45359237
    const val CM_PER_INCH = 2.54
    const val INCHES_PER_FOOT = 12

    fun lbsToKg(lbs: Double): Double = lbs * KG_PER_LB

    fun kgToLbs(kg: Double): Double = kg / KG_PER_LB

    fun inchesToCm(inches: Double): Double = inches * CM_PER_INCH

    fun cmToInches(cm: Double): Double = cm / CM_PER_INCH

    fun feetAndInchesToCm(feet: Int, inches: Double): Double =
        inchesToCm(feet * INCHES_PER_FOOT + inches)

    /** Splits a centimetre height into whole feet plus remaining inches. */
    fun cmToFeetAndInches(cm: Double): FeetInches {
        val totalInches = cmToInches(cm)
        val feet = (totalInches / INCHES_PER_FOOT).toInt()
        return FeetInches(feet = feet, inches = totalInches - feet * INCHES_PER_FOOT)
    }

    /** Converts a weight held in kilograms into the requested display unit. */
    fun kgToDisplay(kg: Double, unit: WeightUnit): Double = when (unit) {
        WeightUnit.KG -> kg
        WeightUnit.LBS -> kgToLbs(kg)
    }

    /** Converts a user-entered weight in [unit] back into canonical kilograms. */
    fun displayToKg(value: Double, unit: WeightUnit): Double = when (unit) {
        WeightUnit.KG -> value
        WeightUnit.LBS -> lbsToKg(value)
    }
}

data class FeetInches(val feet: Int, val inches: Double)
