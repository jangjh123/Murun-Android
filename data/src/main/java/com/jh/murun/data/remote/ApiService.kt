package com.jh.murun.data.remote

import com.jh.murun.data.model.response.MusicResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET("song/random")
    suspend fun fetchMusicByBpm(
        @Query("bpm") bpm: Int
    ): Response<MusicResponse>

    @GET("song")
    suspend fun fetchMusicById(
        @Query("uuid") id: String
    ): Response<MusicResponse>

    @GET
    suspend fun fetchMusicImage(
        @Url url: String
    ): Response<ResponseBody>
}