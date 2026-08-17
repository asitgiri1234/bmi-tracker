package com.asitkg.bmitracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.asitkg.bmitracker.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profiles WHERE owner_uid = :ownerUid ORDER BY created_at_epoch_millis ASC")
    fun observeProfiles(ownerUid: String): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE id = :profileId")
    fun observeProfile(profileId: Long): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles WHERE id = :profileId")
    suspend fun getProfile(profileId: Long): ProfileEntity?

    /** Used to decide whether a signed-in user still needs onboarding. */
    @Query("SELECT COUNT(*) FROM profiles WHERE owner_uid = :ownerUid")
    suspend fun countForOwner(ownerUid: String): Int

    @Query("SELECT * FROM profiles WHERE owner_uid = :ownerUid ORDER BY created_at_epoch_millis ASC LIMIT 1")
    suspend fun firstForOwner(ownerUid: String): ProfileEntity?

    @Insert
    suspend fun insert(profile: ProfileEntity): Long

    @Update
    suspend fun update(profile: ProfileEntity)

    @Delete
    suspend fun delete(profile: ProfileEntity)
}
