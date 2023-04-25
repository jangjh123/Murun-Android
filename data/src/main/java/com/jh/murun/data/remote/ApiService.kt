package com.jh.murun.data.remote

import com.jh.murun.data.base.BaseResponse
import com.jh.murun.data.model.response.MusicResponse
import com.jh.murun.domain.base.BaseModel
import com.jh.murun.domain.model.Music
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET("song")
    suspend fun fetchMusicList(
        @Query("bpm") bpm: Int
    ): Response<BaseResponse>

    @GET("song")
    suspend fun fetchMusicById(
        @Query("uuid") id: String
    ): Response<BaseResponse>

    @GET
    suspend fun fetchMusicFile(
        @Url url: String
    ): Response<ResponseBody>

    @GET
    suspend fun fetchMusicImage(
        @Url url: String
    ): Response<ResponseBody>
}