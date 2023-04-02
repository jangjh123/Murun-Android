package com.jh.presentation.di

import com.jh.murun.data.data_store.DataStoreManager
import com.jh.murun.data.repositoryImpl.SplashRepositoryImpl
import com.jh.murun.domain.repository.SplashRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideSplashRepository(dataStoreManager: DataStoreManager) : SplashRepository = SplashRepositoryImpl(dataStoreManager)
}