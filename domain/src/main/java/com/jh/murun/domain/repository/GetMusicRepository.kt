package com.jh.murun.domain.repository

import com.jh.murun.domain.model.MusicInfo
import com.jh.murun.domain.model.ResponseState
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface GetMusicRepository {
    suspend fun getMusicInfoList(bpm: Int): Flow<ResponseState<List<MusicInfo>>>
    suspend fun getMusicInfoById(id: String): Flow<ResponseState<MusicInfo>>
    suspend fun fetchMusicFile(url: String): Flow<ResponseBody?>
}