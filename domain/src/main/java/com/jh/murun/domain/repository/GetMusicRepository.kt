package com.jh.murun.domain.repository

import com.jh.murun.domain.model.Music
import com.jh.murun.domain.model.ResponseState
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface GetMusicRepository {
    suspend fun getMusicList(bpm: Int): Flow<ResponseState<List<Music>>>
    suspend fun getMusicById(id: String): Flow<ResponseState<Music>>
    suspend fun fetchMusicFile(url: String): Flow<ResponseBody?>
    suspend fun fetchMusicImage(url: String): Flow<ResponseBody?>
}