package com.jh.murun.data.repositoryImpl

import com.jh.murun.data.data_store.DataStoreManager
import com.jh.murun.domain.repository.SplashRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SplashRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : SplashRepository {

    override fun getToSkipOnBoarding(): Flow<Boolean> = dataStoreManager.getToSkipOnBoarding()

    override suspend fun setToSkipOnBoarding() {
        dataStoreManager.setToSkipOnBoarding()
    }
}