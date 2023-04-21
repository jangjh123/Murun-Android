package com.jh.presentation.di

import com.jh.murun.domain.repository.FavoriteRepository
import com.jh.murun.domain.repository.GetMusicRepository
import com.jh.murun.domain.use_case.favorite.GetMusicExistenceInFavoriteListUseCase
import com.jh.murun.domain.use_case.music.GetMusicByIdUseCase
import com.jh.murun.domain.use_case.music.GetMusicFileUseCase
import com.jh.murun.domain.use_case.music.GetMusicImageUseCase
import com.jh.murun.domain.use_case.music.GetMusicListByCadenceUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceScopedUseCaseModule {

    @ServiceScoped
    @Provides
    fun provideGetMusicListUseCase(getMusicRepository: GetMusicRepository) = GetMusicListByCadenceUseCase(getMusicRepository)

    @ServiceScoped
    @Provides
    fun provideGetMusicByIdUseCase(getMusicRepository: GetMusicRepository) = GetMusicByIdUseCase(getMusicRepository)

    @ServiceScoped
    @Provides
    fun provideGetMusicFileUseCase(getMusicRepository: GetMusicRepository) = GetMusicFileUseCase(getMusicRepository)

    @ServiceScoped
    @Provides
    fun provideGetMusicImageUseCase(getMusicRepository: GetMusicRepository) = GetMusicImageUseCase(getMusicRepository)

    @ServiceScoped
    @Provides
    fun provideGetMusicExistenceInFavoriteList(favoriteRepository: FavoriteRepository) = GetMusicExistenceInFavoriteListUseCase(favoriteRepository)
}