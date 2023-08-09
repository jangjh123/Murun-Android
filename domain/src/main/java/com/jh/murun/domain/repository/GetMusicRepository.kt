package com.jh.murun.domain.repository

import com.jh.murun.domain.model.Music
import com.jh.murun.domain.model.ResponseState
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface GetMusicRepository {
    suspend fun fetchMusicListByBpm(bpm: Int): Flow<ResponseState<List<Music>>>
    suspend fun fetchMusicImage(url: String): Flow<ResponseState<ResponseBody>>
}