package com.jh.presentation.di

import com.jh.murun.domain.repository.GetMusicRepository
import com.jh.murun.domain.use_case.music.GetMusicFileUseCase
import com.jh.murun.domain.use_case.music.GetMusicInfoByIdUseCase
import com.jh.murun.domain.use_case.music.GetMusicInfoListByCadenceUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceScopedUseCaseModule {

    @ServiceScoped
    @Provides
    fun provideGetMusicListUseCase(getMusicRepository: GetMusicRepository) = GetMusicInfoListByCadenceUseCase(getMusicRepository)

    @ServiceScoped
    @Provides
    fun provideGetMusicByIdUseCase(getMusicRepository: GetMusicRepository) = GetMusicInfoByIdUseCase(getMusicRepository)

    @ServiceScoped
    @Provides
    fun provideGetMusicFileUseCase(getMusicRepository: GetMusicRepository) = GetMusicFileUseCase(getMusicRepository)
}