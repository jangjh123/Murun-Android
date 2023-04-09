package com.jh.murun.data.remote

import com.jh.murun.data.model.response.MusicInfoResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET("song")
    suspend fun fetchMusicInfoList(
        @Query("bpm") bpm: Int
    ): Response<List<MusicInfoResponse>>

    @GET("song")
    suspend fun fetchMusicInfoById(
        @Query("uuid") id: String
    ): Response<MusicInfoResponse>

    @GET
    suspend fun fetchMusicFile(
        @Url url: String
    ): Response<ResponseBody>
}