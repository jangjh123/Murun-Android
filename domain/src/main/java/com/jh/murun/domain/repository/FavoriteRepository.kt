package com.jh.murun.domain.repository

import com.jh.murun.domain.model.Music
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun readAllMusics(): Flow<List<Music>?>
    suspend fun insertMusicToFavoriteList(music: Music): Flow<Boolean>
    fun deleteMusicFromFavoriteList(music: Music): Flow<Boolean>
    suspend fun updateReorderedFavoriteList(musics: List<Music>)
}