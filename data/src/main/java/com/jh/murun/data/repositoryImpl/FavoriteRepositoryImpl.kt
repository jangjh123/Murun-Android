package com.jh.murun.data.repositoryImpl

import com.jh.murun.data.local.MusicDao
import com.jh.murun.domain.model.Music
import com.jh.murun.domain.repository.FavoriteRepository
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val musicDao: MusicDao
) : FavoriteRepository {
    override suspend fun insertMusicToFavoriteList(music: Music) {
        TODO("Not yet implemented")
    }
}