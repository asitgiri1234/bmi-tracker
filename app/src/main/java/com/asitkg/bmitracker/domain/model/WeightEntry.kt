package com.asitkg.bmitracker.domain.model

import java.time.Instant

/**
 * One recorded weight measurement, always in kilograms.
 *
 * The full series is the weight history graph; its latest element is the
 * profile's current weight.
 */
data class WeightEntry(
    val id: Long = 0L,
    val profileId: Long,
    val weightKg: Double,
    val recordedAt: Instant = Instant.now(),
)
