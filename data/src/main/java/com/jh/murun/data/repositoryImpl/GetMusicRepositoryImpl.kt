package com.jh.murun.data.repositoryImpl

import com.jh.murun.data.model.response.ErrorResponse
import com.jh.murun.data.model.response.ErrorResponse.Companion.toDataModel
import com.jh.murun.data.model.response.MusicInfoResponse.Companion.toDataModel
import com.jh.murun.data.remote.ApiService
import com.jh.murun.data.remote.MurunResponse
import com.jh.murun.data.remote.ResponseHandler
import com.jh.murun.domain.model.MusicInfo
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

    override suspend fun getMusicInfoList(bpm: Int): Flow<ResponseState<List<MusicInfo>>> {
        return flow {
            responseHandler.handle {
                apiService.fetchMusicInfoList(bpm = bpm)
            }.onEach { result ->
                when (result) {
                    is MurunResponse.Success -> emit(ResponseState.Success(result.data.map { it.toDataModel() }))
                    is MurunResponse.Error -> emit(ResponseState.Error(result.error.toDataModel()))
                }
            }.collect()
        }
    }

    override suspend fun getMusicInfoById(id: String): Flow<ResponseState<MusicInfo>> {
        return flow {
            responseHandler.handle {
                apiService.fetchMusicInfoById(id)
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
}