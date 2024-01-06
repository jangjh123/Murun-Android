package com.jh.murun.domain.use_case.favorite

import com.jh.murun.domain.model.Music
import com.jh.murun.domain.repository.FavoriteRepository
import javax.inject.Inject

class UpdateReorderedFavoriteListUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(musics: List<Music>) = favoriteRepository.updateReorderedFavoriteList(musics)
}