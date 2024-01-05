package com.jh.murun.domain.repository

import com.jh.murun.domain.model.Music
import com.jh.murun.domain.model.ResponseState
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface GetMusicRepository {
    fun fetchMusicByBpm(bpm: Int): Flow<ResponseState<Music>>
    fun fetchMusicImage(url: String): Flow<ResponseState<ResponseBody>>
}