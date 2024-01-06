package com.jh.murun.domain.use_case.favorite

import com.jh.murun.domain.repository.FavoriteRepository
import javax.inject.Inject

class GetFavoriteListUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke() = favoriteRepository.readAllMusics()
}