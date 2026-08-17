package com.asitkg.bmitracker.data.mapper

import com.asitkg.bmitracker.data.local.entity.ProfileEntity
import com.asitkg.bmitracker.data.local.entity.WeightEntryEntity
import com.asitkg.bmitracker.domain.model.Gender
import com.asitkg.bmitracker.domain.model.HeightUnit
import com.asitkg.bmitracker.domain.model.Profile
import com.asitkg.bmitracker.domain.model.WeightEntry
import com.asitkg.bmitracker.domain.model.WeightUnit
import java.time.Instant
import java.time.LocalDate

/**
 * Translation between storage rows and domain models.
 *
 * Enums are stored as names rather than ordinals so that reordering an enum
 * cannot silently reinterpret existing rows.
 */

fun ProfileEntity.toDomain(): Profile = Profile(
    id = id,
    ownerUid = ownerUid,
    name = name,
    gender = Gender.fromName(gender),
    dateOfBirth = dateOfBirthEpochDay?.let(LocalDate::ofEpochDay),
    heightCm = heightCm,
    weightUnit = runCatching { WeightUnit.valueOf(weightUnit) }.getOrDefault(WeightUnit.KG),
    heightUnit = runCatching { HeightUnit.valueOf(heightUnit) }.getOrDefault(HeightUnit.CM),
    createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
)

fun Profile.toEntity(): ProfileEntity = ProfileEntity(
    id = id,
    ownerUid = ownerUid,
    name = name,
    gender = gender.name,
    dateOfBirthEpochDay = dateOfBirth?.toEpochDay(),
    heightCm = heightCm,
    weightUnit = weightUnit.name,
    heightUnit = heightUnit.name,
    createdAtEpochMillis = createdAt.toEpochMilli(),
)

fun WeightEntryEntity.toDomain(): WeightEntry = WeightEntry(
    id = id,
    profileId = profileId,
    weightKg = weightKg,
    recordedAt = Instant.ofEpochMilli(recordedAtEpochMillis),
)

fun WeightEntry.toEntity(): WeightEntryEntity = WeightEntryEntity(
    id = id,
    profileId = profileId,
    weightKg = weightKg,
    recordedAtEpochMillis = recordedAt.toEpochMilli(),
)
