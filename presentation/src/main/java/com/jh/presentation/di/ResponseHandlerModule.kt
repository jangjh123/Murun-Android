package com.jh.presentation.di

import com.jh.murun.data.remote.ResponseHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ResponseHandlerModule {

    @Singleton
    @Provides
    fun provideResponseHandler() = ResponseHandler()
}