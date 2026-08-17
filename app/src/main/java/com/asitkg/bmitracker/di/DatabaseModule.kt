package com.asitkg.bmitracker.di

import android.content.Context
import androidx.room.Room
import com.asitkg.bmitracker.data.local.BmiDatabase
import com.asitkg.bmitracker.data.local.dao.ProfileDao
import com.asitkg.bmitracker.data.local.dao.WeightEntryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BmiDatabase =
        Room.databaseBuilder(context, BmiDatabase::class.java, BmiDatabase.NAME).build()

    @Provides
    fun provideProfileDao(database: BmiDatabase): ProfileDao = database.profileDao()

    @Provides
    fun provideWeightEntryDao(database: BmiDatabase): WeightEntryDao = database.weightEntryDao()

    /**
     * Time is injected rather than read from [java.time.Instant.now] at call
     * sites, so date-window logic can be tested deterministically.
     */
    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemDefaultZone()
}
