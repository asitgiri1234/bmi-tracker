package com.asitkg.bmitracker.domain.repository

import com.asitkg.bmitracker.domain.model.WeightEntry
import kotlinx.coroutines.flow.Flow

interface WeightRepository {

    /** Measurements from the last [days] days, oldest first — the graph series. */
    fun observeRecentEntries(profileId: Long, days: Long = 7): Flow<List<WeightEntry>>

    fun observeAllEntries(profileId: Long): Flow<List<WeightEntry>>

    /** The profile's current weight; null until the first measurement exists. */
    fun observeLatest(profileId: Long): Flow<WeightEntry?>

    suspend fun getLatest(profileId: Long): WeightEntry?

    /**
     * Records a measurement. Updating a profile's weight and appending to its
     * history are the same operation, so the graph is always consistent with
     * the displayed BMI.
     */
    suspend fun recordWeight(profileId: Long, weightKg: Double): Long
}
