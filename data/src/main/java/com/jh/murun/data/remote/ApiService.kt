package com.jh.murun.data.remote

import com.jh.murun.data.model.response.MusicResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET("song")
    suspend fun fetchMusicList(
        @Query("bpm") bpm: Int
    ): Response<List<MusicResponse>>

    @GET("song")
    suspend fun fetchMusicById(
        @Query("uuid") id: String
    ): Response<MusicResponse>

    @GET
    suspend fun fetchMusicFile(
        @Url url: String
    ): Response<ResponseBody>

    @GET
    suspend fun fetchMusicImage(
        @Url url: String
    ): Response<ResponseBody>
}