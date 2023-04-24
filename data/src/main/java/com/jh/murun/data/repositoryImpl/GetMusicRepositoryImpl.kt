package com.jh.murun.data.repositoryImpl

import com.jh.murun.data.model.response.ErrorResponse.Companion.toDataModel
import com.jh.murun.data.model.response.MusicResponse.Companion.toDataModel
import com.jh.murun.data.remote.ApiService
import com.jh.murun.data.remote.MurunResponse
import com.jh.murun.data.remote.ResponseHandler
import com.jh.murun.domain.model.Music
import com.jh.murun.domain.model.ResponseState
import com.jh.murun.domain.repository.GetMusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import okhttp3.ResponseBody
import javax.inject.Inject

class GetMusicRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val responseHandler: ResponseHandler
) : GetMusicRepository {

    override suspend fun fetchMusicList(bpm: Int): Flow<ResponseState<List<Music>>> {
        return flow {
            responseHandler.handle {
                apiService.fetchMusicList(bpm = bpm)
            }.onEach { result ->
                when (result) {
                    is MurunResponse.Success -> emit(ResponseState.Success(result.data.map { it.toDataModel() }))
                    is MurunResponse.Error -> emit(ResponseState.Error(result.error.toDataModel()))
                }
            }.collect()
        }
    }

    override suspend fun fetchMusicById(id: String): Flow<ResponseState<Music>> {
        return flow {
            responseHandler.handle {
                apiService.fetchMusicById(id)
            }.onEach { result ->
                when (result) {
                    is MurunResponse.Success -> emit(ResponseState.Success(result.data.toDataModel()))
                    is MurunResponse.Error -> emit(ResponseState.Error(result.error.toDataModel()))
                }
            }.collect()
        }
    }

    override suspend fun fetchMusicFile(url: String): Flow<ResponseBody?> {
        return flow {
            responseHandler.handle {
                apiService.fetchMusicFile(url)
            }.onEach { result ->
                when (result) {
                    is MurunResponse.Success -> emit(result.data)
                    is MurunResponse.Error -> emit(null)
                }
            }.collect()
        }
    }

    override suspend fun fetchMusicImage(url: String): Flow<ResponseBody?> {
        return flow {
            responseHandler.handle {
                apiService.fetchMusicImage(url)
            }.onEach { result ->
                when (result) {
                    is MurunResponse.Success -> emit(result.data)
                    is MurunResponse.Error -> emit(null)
                }
            }.collect()
        }
    }
}