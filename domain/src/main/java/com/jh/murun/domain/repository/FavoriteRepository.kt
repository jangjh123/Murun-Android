package com.jh.murun.domain.repository

import com.jh.murun.domain.model.Music
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun insertMusicToFavoriteList(music: Music): Flow<Boolean>
    suspend fun isMusicExistInFavoriteList(id: String): Flow<Boolean>
}