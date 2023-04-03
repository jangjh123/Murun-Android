package com.jh.murun.domain.repository

import com.jh.murun.domain.model.Music
import com.jh.murun.domain.model.ResponseState
import kotlinx.coroutines.flow.Flow

interface GetMusicRepository {
    suspend fun getMusicList(bpm: Int): Flow<ResponseState<List<Music>>>
}