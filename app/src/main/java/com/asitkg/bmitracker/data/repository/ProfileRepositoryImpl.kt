package com.asitkg.bmitracker.data.repository

import com.asitkg.bmitracker.data.local.dao.ProfileDao
import com.asitkg.bmitracker.data.mapper.toDomain
import com.asitkg.bmitracker.data.mapper.toEntity
import com.asitkg.bmitracker.domain.model.Profile
import com.asitkg.bmitracker.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao,
) : ProfileRepository {

    override fun observeProfiles(ownerUid: String): Flow<List<Profile>> =
        profileDao.observeProfiles(ownerUid).map { rows -> rows.map { it.toDomain() } }

    override fun observeProfile(profileId: Long): Flow<Profile?> =
        profileDao.observeProfile(profileId).map { it?.toDomain() }

    override suspend fun getProfile(profileId: Long): Profile? =
        profileDao.getProfile(profileId)?.toDomain()

    override suspend fun countForOwner(ownerUid: String): Int =
        profileDao.countForOwner(ownerUid)

    override suspend fun firstForOwner(ownerUid: String): Profile? =
        profileDao.firstForOwner(ownerUid)?.toDomain()

    override suspend fun createProfile(profile: Profile): Long =
        profileDao.insert(profile.toEntity())

    override suspend fun updateProfile(profile: Profile) =
        profileDao.update(profile.toEntity())

    override suspend fun deleteProfile(profile: Profile) =
        profileDao.delete(profile.toEntity())
}
