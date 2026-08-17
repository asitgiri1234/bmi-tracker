package com.asitkg.bmitracker.di

import com.asitkg.bmitracker.data.repository.ProfileRepositoryImpl
import com.asitkg.bmitracker.data.repository.WeightRepositoryImpl
import com.asitkg.bmitracker.domain.repository.ProfileRepository
import com.asitkg.bmitracker.domain.repository.WeightRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Binds the domain-layer interfaces to their Room-backed implementations. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindWeightRepository(impl: WeightRepositoryImpl): WeightRepository
}
