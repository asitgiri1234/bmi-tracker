package com.asitkg.bmitracker.domain.repository

import com.asitkg.bmitracker.domain.model.Profile
import kotlinx.coroutines.flow.Flow

/**
 * Declared in the domain layer so use cases and view models depend on this
 * abstraction rather than on Room.
 */
interface ProfileRepository {

    fun observeProfiles(ownerUid: String): Flow<List<Profile>>

    fun observeProfile(profileId: Long): Flow<Profile?>

    suspend fun getProfile(profileId: Long): Profile?

    suspend fun countForOwner(ownerUid: String): Int

    suspend fun firstForOwner(ownerUid: String): Profile?

    /** @return the id assigned to the new profile. */
    suspend fun createProfile(profile: Profile): Long

    suspend fun updateProfile(profile: Profile)

    suspend fun deleteProfile(profile: Profile)
}
