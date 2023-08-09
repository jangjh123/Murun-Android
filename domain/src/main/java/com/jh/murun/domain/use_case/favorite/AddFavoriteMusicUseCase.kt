package com.jh.murun.domain.use_case.favorite

import com.jh.murun.domain.model.Music
import com.jh.murun.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddFavoriteMusicUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(music: Music) : Flow<Boolean> = favoriteRepository.insertMusicToFavoriteList(music)
}