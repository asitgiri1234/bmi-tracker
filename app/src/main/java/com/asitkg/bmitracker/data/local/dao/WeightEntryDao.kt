package com.asitkg.bmitracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.asitkg.bmitracker.data.local.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightEntryDao {

    /** Ascending so chart data arrives in plot order. */
    @Query(
        """
        SELECT * FROM weight_entries
        WHERE profile_id = :profileId AND recorded_at_epoch_millis >= :sinceEpochMillis
        ORDER BY recorded_at_epoch_millis ASC
        """,
    )
    fun observeEntriesSince(profileId: Long, sinceEpochMillis: Long): Flow<List<WeightEntryEntity>>

    @Query("SELECT * FROM weight_entries WHERE profile_id = :profileId ORDER BY recorded_at_epoch_millis ASC")
    fun observeAllEntries(profileId: Long): Flow<List<WeightEntryEntity>>

    /** The profile's current weight; null before the first measurement. */
    @Query("SELECT * FROM weight_entries WHERE profile_id = :profileId ORDER BY recorded_at_epoch_millis DESC LIMIT 1")
    fun observeLatest(profileId: Long): Flow<WeightEntryEntity?>

    @Query("SELECT * FROM weight_entries WHERE profile_id = :profileId ORDER BY recorded_at_epoch_millis DESC LIMIT 1")
    suspend fun getLatest(profileId: Long): WeightEntryEntity?

    @Insert
    suspend fun insert(entry: WeightEntryEntity): Long

    @Query("DELETE FROM weight_entries WHERE profile_id = :profileId")
    suspend fun deleteAllForProfile(profileId: Long)
}
