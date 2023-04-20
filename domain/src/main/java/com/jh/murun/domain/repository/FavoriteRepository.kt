package com.jh.murun.domain.repository

import com.jh.murun.domain.model.Music

interface FavoriteRepository {
    suspend fun insertMusicToFavoriteList(music: Music)
}