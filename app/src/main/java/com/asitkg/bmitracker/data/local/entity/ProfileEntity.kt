package com.asitkg.bmitracker.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "profiles",
    // Profiles are almost always queried by owner, so this index carries the
    // multi-user list query.
    indices = [Index("owner_uid")],
)
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "owner_uid")
    val ownerUid: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "gender")
    val gender: String,

    /** Epoch day, or null when not supplied. Date-only, so no timezone drift. */
    @ColumnInfo(name = "date_of_birth_epoch_day")
    val dateOfBirthEpochDay: Long?,

    @ColumnInfo(name = "height_cm")
    val heightCm: Double,

    @ColumnInfo(name = "weight_unit")
    val weightUnit: String,

    @ColumnInfo(name = "height_unit")
    val heightUnit: String,

    @ColumnInfo(name = "created_at_epoch_millis")
    val createdAtEpochMillis: Long,
)
