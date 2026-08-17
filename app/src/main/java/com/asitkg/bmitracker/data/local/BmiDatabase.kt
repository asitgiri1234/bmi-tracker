package com.asitkg.bmitracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.asitkg.bmitracker.data.local.dao.ProfileDao
import com.asitkg.bmitracker.data.local.dao.WeightEntryDao
import com.asitkg.bmitracker.data.local.entity.ProfileEntity
import com.asitkg.bmitracker.data.local.entity.WeightEntryEntity

@Database(
    entities = [ProfileEntity::class, WeightEntryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class BmiDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao

    abstract fun weightEntryDao(): WeightEntryDao

    companion object {
        const val NAME = "bmi_tracker.db"
    }
}
