package com.jh.murun.data.remote

import com.jh.murun.data.model.response.MusicResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("song")
    suspend fun fetchMusicList(
        @Query("bpm") bpm: Int
    ): Response<List<MusicResponse>>

    @GET("song")
    suspend fun fetchMusicById(
        @Query("uuid") id: String
    ) : Response<MusicResponse>
}