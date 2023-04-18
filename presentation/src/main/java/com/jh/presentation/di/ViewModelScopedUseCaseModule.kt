package com.jh.presentation.di

import com.jh.murun.domain.repository.SplashRepository
import com.jh.murun.domain.use_case.splash.GetToSkipOnBoardingUseCase
import com.jh.murun.domain.use_case.splash.SetToSkipOnBoardingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelScopedUseCaseModule {

    @ViewModelScoped
    @Provides
    fun provideGetToSkipOnBoardingUseCase(splashRepository: SplashRepository) = GetToSkipOnBoardingUseCase(splashRepository)

    @ViewModelScoped
    @Provides
    fun provideSetToSkipOnBoardingUseCase(splashRepository: SplashRepository) = SetToSkipOnBoardingUseCase(splashRepository)
}