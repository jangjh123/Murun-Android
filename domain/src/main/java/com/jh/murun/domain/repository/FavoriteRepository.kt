package com.jh.murun.domain.repository

import com.jh.murun.domain.model.Music
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun readAllMusics(): Flow<List<Music>?>
    suspend fun insertMusicToFavoriteList(music: Music): Flow<Boolean>
    suspend fun isMusicExistsInFavoriteList(id: String): Flow<Boolean>
    suspend fun deleteMusicFromFavoriteList(id: String): Flow<Boolean>
}