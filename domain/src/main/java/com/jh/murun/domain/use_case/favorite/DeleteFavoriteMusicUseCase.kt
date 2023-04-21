package com.jh.murun.domain.use_case.favorite

import com.jh.murun.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteFavoriteMusicUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(id: String): Flow<Boolean> = favoriteRepository.deleteMusicFromFavoriteList(id)
}