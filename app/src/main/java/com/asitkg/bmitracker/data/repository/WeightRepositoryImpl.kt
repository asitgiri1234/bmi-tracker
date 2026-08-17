package com.asitkg.bmitracker.data.repository

import com.asitkg.bmitracker.data.local.dao.WeightEntryDao
import com.asitkg.bmitracker.data.mapper.toDomain
import com.asitkg.bmitracker.data.mapper.toEntity
import com.asitkg.bmitracker.domain.model.WeightEntry
import com.asitkg.bmitracker.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeightRepositoryImpl @Inject constructor(
    private val weightEntryDao: WeightEntryDao,
    // Injected so tests can pin "now" instead of depending on the wall clock.
    private val clock: Clock,
) : WeightRepository {

    override fun observeRecentEntries(profileId: Long, days: Long): Flow<List<WeightEntry>> {
        val since = clock.instant().minus(Duration.ofDays(days)).toEpochMilli()
        return weightEntryDao.observeEntriesSince(profileId, since)
            .map { rows -> rows.map { it.toDomain() } }
    }

    override fun observeAllEntries(profileId: Long): Flow<List<WeightEntry>> =
        weightEntryDao.observeAllEntries(profileId).map { rows -> rows.map { it.toDomain() } }

    override fun observeLatest(profileId: Long): Flow<WeightEntry?> =
        weightEntryDao.observeLatest(profileId).map { it?.toDomain() }

    override suspend fun getLatest(profileId: Long): WeightEntry? =
        weightEntryDao.getLatest(profileId)?.toDomain()

    override suspend fun recordWeight(profileId: Long, weightKg: Double): Long =
        weightEntryDao.insert(
            WeightEntry(
                profileId = profileId,
                weightKg = weightKg,
                recordedAt = clock.instant(),
            ).toEntity(),
        )
}
