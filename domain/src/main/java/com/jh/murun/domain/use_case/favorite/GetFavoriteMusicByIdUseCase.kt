package com.jh.murun.domain.use_case.favorite

import com.jh.murun.domain.repository.FavoriteRepository
import javax.inject.Inject

class GetFavoriteMusicByIdUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(id: String) = favoriteRepository.getMusicById(id)
}