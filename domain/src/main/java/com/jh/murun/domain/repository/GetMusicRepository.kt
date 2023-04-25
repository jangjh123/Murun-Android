package com.jh.murun.domain.repository

import com.jh.murun.domain.base.BaseModel
import com.jh.murun.domain.model.Music
import com.jh.murun.domain.model.ResponseState
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface GetMusicRepository {
    suspend fun fetchMusicList(bpm: Int): Flow<ResponseState<BaseModel>>
    suspend fun fetchMusicById(id: String): Flow<ResponseState<BaseModel>>
    suspend fun fetchMusicFile(url: String): Flow<ResponseState<ResponseBody>>
    suspend fun fetchMusicImage(url: String): Flow<ResponseState<ResponseBody>>
}