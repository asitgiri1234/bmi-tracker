package com.asitkg.bmitracker.domain.model

import java.time.Instant
import java.time.LocalDate
import java.time.Period

/**
 * A person being tracked.
 *
 * Several profiles can belong to one signed-in account ([ownerUid]), which is
 * how multi-user support works: switching profile changes whose data is shown
 * without signing anyone out.
 *
 * Note there is no weight field — current weight is the most recent
 * [WeightEntry], so updating weight and recording history are the same action.
 */
data class Profile(
    val id: Long = 0L,
    val ownerUid: String,
    val name: String,
    val gender: Gender,
    val dateOfBirth: LocalDate?,
    val heightCm: Double,
    val weightUnit: WeightUnit = WeightUnit.KG,
    val heightUnit: HeightUnit = HeightUnit.CM,
    val createdAt: Instant = Instant.now(),
) {
    /** Null when no date of birth was supplied, since age is optional. */
    fun ageYears(today: LocalDate = LocalDate.now()): Int? =
        dateOfBirth?.let { Period.between(it, today).years }
}
