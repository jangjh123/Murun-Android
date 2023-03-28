package com.jh.murun.domain.repository

import kotlinx.coroutines.flow.Flow

interface SplashRepository {

    fun getToSkipOnBoarding(): Flow<Boolean>

    suspend fun setToSkipOnBoarding()
}